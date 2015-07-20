package org.paninij.soter.transfer;

import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;

import com.ibm.wala.ipa.cha.IClassHierarchy;

public class TransferAnalysisFactory
{
    protected final IClassHierarchy cha;
    
    public TransferAnalysisFactory(IClassHierarchy cha)
    {
        this.cha = cha;
    }

    public TransferAnalysis make(CapsuleTemplate template, CallGraphAnalysis cga)
    {
        return new TransferAnalysis(template, cga, cha);
    }
}
