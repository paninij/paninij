package org.paninij.soter.live;

import java.util.HashMap;
import java.util.Map;

import org.paninij.soter.cfa.CallGraphAnalysis;

import com.ibm.wala.ipa.callgraph.CGNode;

/**
 * Used to perform standard local live variable analyses on given call graph nodes. Note that this
 * factory memoizes all of its results until `clearCache()` is called.
 */
public class LocalLiveAnalysisFactory
{
    CallGraphAnalysis cfa;
    Map<CGNode, LocalLiveAnalysis> cachedAnalyses;

    public LocalLiveAnalysisFactory(CallGraphAnalysis cfa)
    {
        this.cfa = cfa;
        cachedAnalyses = new HashMap<CGNode, LocalLiveAnalysis>();
    }
    
    public LocalLiveAnalysis lookupOrMake(CGNode node)
    {
        LocalLiveAnalysis analysis = cachedAnalyses.get(node);
        if (analysis == null) {
            analysis = new LocalLiveAnalysis(node, cfa);
            cachedAnalyses.put(node, analysis);
        }
        return analysis;
    }
}
