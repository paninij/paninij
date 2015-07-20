package org.paninij.soter.live;

import org.paninij.soter.cfa.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;

import com.ibm.wala.ipa.cha.IClassHierarchy;

public class TransferSitesLiveAnalysisFactory
{
    protected final IClassHierarchy cha;

    public TransferSitesLiveAnalysisFactory(IClassHierarchy cha)
    {
        this.cha = cha;
    }

    public TransferSitesLiveAnalysis make(CapsuleTemplate template, CallGraphAnalysis cfa)
    {
        return new TransferSitesLiveAnalysis(template, new LocalLiveAnalysisFactory(cfa),
                                             new TransferSitesAnalysis(template, cfa, cha),
                                             cfa, cha);
    }
}
