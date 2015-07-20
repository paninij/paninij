package org.paninij.soter.live;

import org.paninij.soter.cfa.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.transfer.TransferAnalysis;
import org.paninij.soter.transfer.TransferAnalysisFactory;

import com.ibm.wala.ipa.cha.IClassHierarchy;

public class CallGraphLiveAnalysisFactory
{
    protected final IClassHierarchy cha;

    protected final TransferAnalysisFactory transferAnalysisFactory;
    protected final TransferLiveAnalysisFactory transferLiveAnalysisFactory;

    public CallGraphLiveAnalysisFactory(IClassHierarchy cha)
    {
        this.cha = cha;

        transferAnalysisFactory = new TransferAnalysisFactory(cha);
        transferLiveAnalysisFactory = new TransferLiveAnalysisFactory(cha);
    }

    public CallGraphLiveAnalysis make(CapsuleTemplate template, CallGraphAnalysis cfa)
    {
        TransferAnalysis ta = transferAnalysisFactory.make(template, cfa);
        TransferLiveAnalysis tla = transferLiveAnalysisFactory.make(template, cfa);
        return new CallGraphLiveAnalysis(template, cfa, ta, tla, cha);
    }
}
