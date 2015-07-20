package org.paninij.soter.live;

import org.paninij.soter.cfa.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.transfer.TransferAnalysis;

import com.ibm.wala.ipa.cha.IClassHierarchy;

public class TransferLiveAnalysisFactory
{
    protected final IClassHierarchy cha;

    public TransferLiveAnalysisFactory(IClassHierarchy cha)
    {
        this.cha = cha;
    }

    public TransferLiveAnalysis make(CapsuleTemplate template, CallGraphAnalysis cfa)
    {
        return new TransferLiveAnalysis(template, new LocalLiveAnalysisFactory(cfa),
                                             new TransferAnalysis(template, cfa, cha),
                                             cfa, cha);
    }
}
