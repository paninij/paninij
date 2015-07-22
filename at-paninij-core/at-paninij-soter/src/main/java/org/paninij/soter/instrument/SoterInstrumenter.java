package org.paninij.soter.instrument;

import static java.io.File.pathSeparator;

import java.io.FileOutputStream;

import org.paninij.soter.SoterAnalysis;
import org.paninij.soter.model.CapsuleTemplate;

import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeCT.ClassWriter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;

public class SoterInstrumenter
{
    protected final CapsuleTemplate template;
    protected final SoterAnalysis sa;
    protected final String outputDir;
    protected final ClassInstrumenter instrumenter;  // Instrumenter of the capsule template class.
    
    protected final String outputFilePath;

    public SoterInstrumenter(CapsuleTemplate template, String outputDir, SoterAnalysis sa,
                             ClassInstrumenter instrumenter) throws InvalidClassFileException
    {
        this.template = template;
        this.sa = sa;
        this.outputDir = outputDir;
        this.instrumenter = instrumenter;
        
        outputFilePath = outputDir + pathSeparator + instrumenter.getReader().getName() + ".class";
    }

    public void perform()
    {
        // TODO: Everything!
    }
    
    public void writeInstrumentedClassFile()
    {
        try {
            ClassWriter classWriter = instrumenter.emitClass();
            FileOutputStream outputStream = new FileOutputStream(outputFilePath);
            outputStream.write(classWriter.makeBytes());
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to write the instrumented class file: " + ex);
        }
    }
}
