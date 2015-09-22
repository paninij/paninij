package org.paninij.soter.instrument;

import static java.io.File.separator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.paninij.runtime.util.IdentitySet;
import org.paninij.soter.SoterAnalysis;
import org.paninij.soter.site.TransferringSite;
import org.paninij.soter.util.Instrumenter;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;


public class SoterInstrumenter extends Instrumenter
{
    protected final SoterAnalysis sa;

    public SoterInstrumenter(ClassInstrumenter templateInstrumenter, SoterAnalysis sa, String outDir)
                                                                     throws InvalidClassFileException
    {
        super(templateInstrumenter, outDir + separator + templateInstrumenter.getReader().getName() + ".class");
        this.sa = sa;
    }

    @Override
    public void performInstrumentation() throws InvalidClassFileException
    {
        assert sa.hasBeenPerformed();
        
        Map<String, Set<TransferringSite>> sitesToInstrument = getSitesToInstrument();
        if (sitesToInstrument.isEmpty()) {
            return;  // Return early if there are no sites that need instrumentation.
        }
        walaInstrumenter.visitMethods(new TransferringSiteInstrumenter(sitesToInstrument));
    }
    
    protected Map<String, Set<TransferringSite>> getSitesToInstrument()
    {
        Map<String, Set<TransferringSite>> sitesToInstrument;
        sitesToInstrument = new HashMap<String, Set<TransferringSite>>();

        for (Entry<IMethod, IdentitySet<TransferringSite>> entry : sa.getUnsafeTransferSitesMap()
                                                                     .entrySet())
        {
            String signature = "L" + entry.getKey().getSignature();
            sitesToInstrument.put(signature, entry.getValue().cloneAsSet());
        }
        
        return sitesToInstrument;
    }
}
