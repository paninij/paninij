package org.paninij.soter.instrument;

import static java.io.File.separator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.paninij.runtime.util.IdentitySet;
import org.paninij.soter.SoterAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.site.TransferringSite;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeClass;
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
    protected final ShrikeClass shrikeClass;
    protected final Map<String, IdentitySet<TransferringSite>> unsafeTransferSitesMap;
    protected final MethodInstrumenter methodInstrumenter;

    public SoterInstrumenter(CapsuleTemplate template, String outputDir, SoterAnalysis sa,
                             ClassInstrumenter instrumenter) throws InvalidClassFileException
    {
        this.template = template;
        this.sa = sa;
        this.outputDir = outputDir;
        this.instrumenter = instrumenter;

        outputFilePath = outputDir + separator + instrumenter.getReader().getName() + ".class";
        shrikeClass = (ShrikeClass) template.getTemplateClass();
        unsafeTransferSitesMap = new HashMap<String, IdentitySet<TransferringSite>>();
        methodInstrumenter = new MethodInstrumenter(this);
    }

    public void perform()
    {
        try
        {
            buildUnsafeTransferSitesMap();
            if (unsafeTransferSitesMap.isEmpty()) {
                return;  // Return if there are no transfer sites that need instrumentation.
            }
            
            instrumenter.visitMethods(methodInstrumenter);
            writeInstrumentedClassFile();
        }
        catch (InvalidClassFileException | IOException ex) {
            throw new RuntimeException("Failed to perform soter instrumentation: " + ex, ex);
        }
    }
    
    protected void buildUnsafeTransferSitesMap()
    {
        for (Entry<IMethod, IdentitySet<TransferringSite>> entry : sa.getUnsafeTransferSitesMap().entrySet())
        {
            String signature = "L" + entry.getKey().getSignature();
            unsafeTransferSitesMap.put(signature, entry.getValue());
        }
    }
    
    protected void writeInstrumentedClassFile() throws InvalidClassFileException, IOException
    {
        ClassWriter classWriter = instrumenter.emitClass();
        FileOutputStream outputStream = new FileOutputStream(outputFilePath);
        outputStream.write(classWriter.makeBytes());
        outputStream.flush();
        outputStream.close();
    }
}
