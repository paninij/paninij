package org.paninij.soter.live;

import java.util.HashMap;
import java.util.Map;

import org.paninij.soter.cga.CallGraphAnalysis;

import com.ibm.wala.ipa.callgraph.CGNode;

/**
 * Used to perform standard local live variable analyses on given call graph nodes. Note that this
 * factory memoizes all of its results until `clearCache()` is called.
 */
public class LocalLiveAnalysisFactory
{
    CallGraphAnalysis cga;
    Map<CGNode, LocalLiveAnalysis> cachedAnalyses;

    public LocalLiveAnalysisFactory(CallGraphAnalysis cga)
    {
        this.cga = cga;
        resetAnalysisCache();
    }
    
    /**
     * Creates a local live analysis instance over the given call graph node if this method has
     * never been called with this node before; otherwise, a previously created (i.e. cached)
     * analysis instance is returned.
     * 
     * Note that newly created analyses have not been performed (i.e. it is the responsibility of
     * the client to call `LocalLiveAnalysis.perform()`.
     */
    public LocalLiveAnalysis lookupOrMake(CGNode node)
    {
        LocalLiveAnalysis analysis = cachedAnalyses.get(node);
        if (analysis == null) {
            analysis = new LocalLiveAnalysis(node, cga);
            cachedAnalyses.put(node, analysis);
        }
        return analysis;
    }

    public void resetAnalysisCache()
    {
        cachedAnalyses = new HashMap<CGNode, LocalLiveAnalysis>();
    }
}
