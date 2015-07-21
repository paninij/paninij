package org.paninij.soter.live;

import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.transfer.TransferAnalysis;

import com.ibm.wala.ipa.cha.IClassHierarchy;

public class CallGraphLiveAnalysisFactory
{
    protected final IClassHierarchy cha;

    public CallGraphLiveAnalysisFactory(IClassHierarchy cha)
    {
        this.cha = cha;
    }

    public CallGraphLiveAnalysis make(CapsuleTemplate template, CallGraphAnalysis cga,
                                      TransferAnalysis ta, TransferLiveAnalysis tla)
    {
        return new CallGraphLiveAnalysis(template, cga, ta, tla, cha);
    }
}
