package org.paninij.soter.cfa;

import org.paninij.soter.model.CapsuleTemplate;

import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class CallGraphAnalysisFactory
{
    protected final IClassHierarchy cha;
    protected final AnalysisOptions options;
    
    public CallGraphAnalysisFactory(IClassHierarchy cha, AnalysisOptions options)
    {
        this.cha = cha;
        this.options = options;
    }

    public CallGraphAnalysis make(CapsuleTemplate template)
    {
        return new CallGraphAnalysis(cha, options);
    }
}
