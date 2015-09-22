package org.paninij.soter.util;

import java.io.FileOutputStream;
import java.io.IOException;

import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeCT.ClassWriter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;

public abstract class Instrumenter implements IdempotentOperation
{
    protected boolean hasBeenPerformed = false;
    
    protected final ClassInstrumenter walaInstrumenter;
    protected final String outputFilePath;
    
    protected Instrumenter(ClassInstrumenter walaInstrumenter, String outputFilePath)
    {
        this.walaInstrumenter = walaInstrumenter;
        this.outputFilePath = outputFilePath;
    }

    /**
     * If this method has never been called before, then any sub-analyses and the main analysis are
     * performed. If this method has been previously called, then it will return immediately.
     * 
     * Warning: implementers of `Instrumenter` should not not generally override `perform()`. They
     * are expected to override `performInstrumentation()` and `writeInstrumentedClassFile()`.
     */
    public void perform()
    {
        if (hasBeenPerformed) {
            return;
        }
        try {
            performInstrumentation();
            writeInstrumentedClassFile();
        } catch (InvalidClassFileException ex) {
            throw new RuntimeException("Failed to perform an instrumentation: " + ex, ex);
        }
        hasBeenPerformed = true;
        assert checkPostConditions();
    }


     /**
     * Calling this performs the main instrumentation which a `Instrumenter` class provides. After
     * this is called, all instrumentation should have been performed. Note that implementations of
     * this method are not expected to be idempotent, because the `perform()` wrapper is provided to
     * provide idempotency checking.
     */
    protected abstract void performInstrumentation() throws InvalidClassFileException;
    

    protected void writeInstrumentedClassFile() throws InvalidClassFileException
    {
        try {
            ClassWriter classWriter = walaInstrumenter.emitClass();
            FileOutputStream outputStream = new FileOutputStream(outputFilePath);
            outputStream.write(classWriter.makeBytes());
            outputStream.flush();
            outputStream.close();
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to perform an instrumentation: " + ex, ex);
        }
    }


    protected boolean checkPostConditions()
    {
        // By default, assume that there are no post-conditions. Do nothing.
        return true;
    }
}
