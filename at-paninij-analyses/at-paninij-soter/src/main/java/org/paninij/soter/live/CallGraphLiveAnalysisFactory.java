package org.paninij.soter.live;

import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.site.SiteAnalysis;

import com.ibm.wala.ipa.cha.IClassHierarchy;

public class CallGraphLiveAnalysisFactory
{
    protected final IClassHierarchy cha;

    public CallGraphLiveAnalysisFactory(IClassHierarchy cha)
    {
        this.cha = cha;
    }

    public CallGraphLiveAnalysis make(CapsuleTemplate template, CallGraphAnalysis cga,
                                      SiteAnalysis sa, TransferLiveAnalysis tla)
    {
        return new CallGraphLiveAnalysis(template, cga, sa, tla, cha);
    }
}
