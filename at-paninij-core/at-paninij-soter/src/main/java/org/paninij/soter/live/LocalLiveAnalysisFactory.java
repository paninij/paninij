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
