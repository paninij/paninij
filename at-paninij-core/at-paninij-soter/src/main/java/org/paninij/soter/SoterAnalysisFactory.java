package org.paninij.soter;

import org.paninij.soter.cfa.CallGraphAnalysis;
import org.paninij.soter.cfa.CallGraphAnalysisFactory;
import org.paninij.soter.live.TransferLiveAnalysis;
import org.paninij.soter.live.TransferLiveAnalysisFactory;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.model.CapsuleTemplateFactory;
import org.paninij.soter.transfer.TransferAnalysis;
import org.paninij.soter.transfer.TransferAnalysisFactory;
import org.paninij.soter.util.WalaUtil;

import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.cha.IClassHierarchy;

/**
 * A factory for creating and performing `@PaniniJ` SOTER analyses. It caches resources that can be
 * used across multiple SOTER analyses (e.g. the class hierarchy analysis).
 */
public class SoterAnalysisFactory
{
    protected final IClassHierarchy cha;
    protected final AnalysisOptions options;
    protected final CapsuleTemplateFactory capsuleTemplateFactory;
    protected final CallGraphAnalysisFactory callGraphAnalysisFactory;
    protected final TransferAnalysisFactory transferSitesAnalysisFactory;
    protected final TransferLiveAnalysisFactory transferSitesLiveAnalysisFactory;

    public SoterAnalysisFactory(String classPath)
    {
        WalaUtil.checkRequiredResourcesExist();

        cha = WalaUtil.makeClassHierarchy(classPath);
        options = WalaUtil.makeAnalysisOptions(cha);
        
        capsuleTemplateFactory = new CapsuleTemplateFactory(cha);
        callGraphAnalysisFactory = new CallGraphAnalysisFactory(cha, options);
        transferSitesAnalysisFactory = new TransferAnalysisFactory(cha);
        transferSitesLiveAnalysisFactory = new TransferLiveAnalysisFactory(cha);
    }
    
    /**
     * @param capsuleName A fully qualified name of a capsule (e.g. "org.paninij.examples.pi.Pi").
     */
    public SoterAnalysis make(String capsuleName)
    {
        CapsuleTemplate template = capsuleTemplateFactory.make(capsuleName);
        CallGraphAnalysis cfa = callGraphAnalysisFactory.make(template);
        TransferAnalysis tsa = transferSitesAnalysisFactory.make(template, cfa);
        TransferLiveAnalysis tsla = transferSitesLiveAnalysisFactory.make(template, cfa);

        //return new TransferAnalysis(template, cfa, /* TODO */, cha);
        throw new UnsupportedOperationException("TODO");
    }
}