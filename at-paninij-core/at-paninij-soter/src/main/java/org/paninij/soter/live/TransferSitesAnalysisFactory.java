package org.paninij.soter.live;

import org.paninij.soter.cfa.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;

import com.ibm.wala.ipa.cha.IClassHierarchy;

public class TransferSitesAnalysisFactory
{
    protected final IClassHierarchy cha;
    
    public TransferSitesAnalysisFactory(IClassHierarchy cha)
    {
        this.cha = cha;
    }

    public TransferSitesAnalysis make(CapsuleTemplate template, CallGraphAnalysis cfa)
    {
        return new TransferSitesAnalysis(template, cfa, cha);
    }
}
