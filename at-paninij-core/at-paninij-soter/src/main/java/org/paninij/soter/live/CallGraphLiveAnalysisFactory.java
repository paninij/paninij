package org.paninij.soter.live;

import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.transfer.TransferAnalysis;
import org.paninij.soter.transfer.TransferAnalysisFactory;

import com.ibm.wala.ipa.cha.IClassHierarchy;

public class CallGraphLiveAnalysisFactory
{
    protected final IClassHierarchy cha;

    protected final TransferAnalysisFactory taFactory;
    protected final TransferLiveAnalysisFactory tlaFactory;

    public CallGraphLiveAnalysisFactory(IClassHierarchy cha)
    {
        this.cha = cha;

        taFactory = new TransferAnalysisFactory(cha);
        tlaFactory = new TransferLiveAnalysisFactory(cha);
    }

    public CallGraphLiveAnalysis make(CapsuleTemplate template, CallGraphAnalysis cga)
    {
        TransferAnalysis ta = taFactory.make(template, cga);
        TransferLiveAnalysis tla = tlaFactory.make(template, cga, ta);
        return new CallGraphLiveAnalysis(template, cga, ta, tla, cha);
    }
}
