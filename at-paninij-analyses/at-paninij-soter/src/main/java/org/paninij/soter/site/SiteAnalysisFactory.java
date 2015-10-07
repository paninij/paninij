package org.paninij.soter.site;

import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;

import com.ibm.wala.ipa.cha.IClassHierarchy;

public class SiteAnalysisFactory
{
    protected final IClassHierarchy cha;
    
    public SiteAnalysisFactory(IClassHierarchy cha)
    {
        this.cha = cha;
    }

    public SiteAnalysis make(CapsuleTemplate template, CallGraphAnalysis cga)
    {
        return new SiteAnalysis(template, cga, cha);
    }
}
