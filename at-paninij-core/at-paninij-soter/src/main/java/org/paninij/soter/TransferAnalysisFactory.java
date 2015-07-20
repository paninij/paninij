package org.paninij.soter;

import org.paninij.soter.cfa.CallGraphAnalysis;
import org.paninij.soter.cfa.CallGraphAnalysisFactory;
import org.paninij.soter.live.TransferSitesLiveAnalysisFactory;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.model.CapsuleTemplateFactory;
import org.paninij.soter.util.WalaUtil;

import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.cha.IClassHierarchy;

/**
 * A factory for creating and performing `@PaniniJ` SOTER analyses. It caches resources that can be
 * used across multiple SOTER analyses (e.g. the class hierarchy analysis).
 */
public class TransferAnalysisFactory
{
    protected final IClassHierarchy cha;
    protected final AnalysisOptions options;
    protected final CapsuleTemplateFactory capsuleTemplateFactory;
    protected final CallGraphAnalysisFactory callGraphAnalysisFactory;
    protected final TransferSitesLiveAnalysisFactory transferSitesLiveAnalysisFactory;

    public TransferAnalysisFactory(String classPath)
    {
        WalaUtil.checkRequiredResourcesExist();

        cha = WalaUtil.makeClassHierarchy(classPath);
        options = WalaUtil.makeAnalysisOptions(cha);
        
        capsuleTemplateFactory = new CapsuleTemplateFactory(cha);
        callGraphAnalysisFactory = new CallGraphAnalysisFactory(cha, options);
        transferSitesLiveAnalysisFactory = new TransferSitesLiveAnalysisFactory(cha);
    }
    
    /**
     * @param capsuleName A fully qualified name of a capsule (e.g. "org.paninij.examples.pi.Pi").
     */
    public TransferAnalysis make(String capsuleName)
    {
        CapsuleTemplate template = capsuleTemplateFactory.make(capsuleName);
        CallGraphAnalysis cfa = callGraphAnalysisFactory.make(template);
        return new TransferAnalysis(template, cfa, cha);
    }
}