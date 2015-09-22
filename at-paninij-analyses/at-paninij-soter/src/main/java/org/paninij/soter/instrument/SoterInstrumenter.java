package org.paninij.soter.instrument;

import static java.io.File.separator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.paninij.runtime.util.IdentitySet;
import org.paninij.soter.SoterAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.site.TransferringSite;
import org.paninij.soter.util.Instrumenter;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeClass;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;


public class SoterInstrumenter extends Instrumenter
{
    protected final CapsuleTemplate template;
    protected final SoterAnalysis sa;
    protected final String outputDir;

    protected final ShrikeClass shrikeClass;

    public SoterInstrumenter(ClassInstrumenter walaInstrumenter, CapsuleTemplate template,
                             String outputDir, SoterAnalysis sa) throws InvalidClassFileException
    {
        super(walaInstrumenter, outputDir + separator + walaInstrumenter.getReader().getName() + ".class");

        this.template = template;
        this.sa = sa;
        this.outputDir = outputDir;

        shrikeClass = (ShrikeClass) template.getTemplateClass();
    }

    @Override
    public void performInstrumentation() throws InvalidClassFileException
    {
        Map<String, IdentitySet<TransferringSite>> sitesToInstrument = getSitesToInstrument();
        if (sitesToInstrument.isEmpty()) {
            return;  // Return early if there are no sites that need instrumentation.
        }
        walaInstrumenter.visitMethods(new TransferringSiteInstrumenter(sitesToInstrument));
    }
    
    protected Map<String, IdentitySet<TransferringSite>> getSitesToInstrument()
    {
        Map<String, IdentitySet<TransferringSite>> sitesToInstrument;
        sitesToInstrument = new HashMap<String, IdentitySet<TransferringSite>>();

        for (Entry<IMethod, IdentitySet<TransferringSite>> entry : sa.getUnsafeTransferSitesMap()
                                                                     .entrySet())
        {
            String signature = "L" + entry.getKey().getSignature();
            sitesToInstrument.put(signature, entry.getValue());
        }
        
        return sitesToInstrument;
    }

}
