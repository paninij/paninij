package org.paninij.soter.instrument;

import static java.io.File.separator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.paninij.soter.site.TransferringSite;
import org.paninij.soter.transfer.SiteAnalysis;
import org.paninij.soter.util.Instrumenter;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;

public class AllTransferringSitesInstrumenter extends Instrumenter
{
    SiteAnalysis sa;
    
    public AllTransferringSitesInstrumenter(ClassInstrumenter templateInstrumenter,
                                            SiteAnalysis sa, String outDir)
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
        
        for (CGNode node: sa.getTransferringNodes())
        {
            String signature = "L" + node.getMethod().getSignature();
            addAllInto(sitesToInstrument, signature, sa.getTransferringSites(node));
        }

        return sitesToInstrument;
    }
    
    /**
     * Adds all of the given values into the set stored within `map` under `key`. If no such nested
     * set exists, then one will be created and all of the values will be added.
     */
    private static <K, V> void addAllInto(Map<K, Set<V>> map, K key, Set<V> values)
    {
        Set<V> nested = map.get(key);
        if (nested == null) {
            nested = new HashSet<V>();
        }
        for (V v : values) {
            nested.add(v);
        }
        map.put(key, nested);
    }
}
