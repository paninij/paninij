package org.paninij.soter.live;

import java.util.Map;
import java.util.Set;

import org.paninij.soter.cfa.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.model.TransferSite;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.ISSABasicBlock;

/**
 * When performed, the analysis analyzes the capsule template and the call graph analysis to
 * generate information about the variables which are alive at the capsule's "relevant" transfer
 * sites and call sites.
 * 
 * See Figure 8 of Negara, 2011.
 */
public class TransferSitesLiveAnalysis
{
    // The analysis's dependencies:
    final protected CapsuleTemplate capsuleTemplate;
    final protected LocalLiveAnalysisFactory localLiveAnalysisFactory;
    final protected TransferSitesAnalysis transferSitesAnalysis;
    final protected CallGraphAnalysis cfa;
    final protected IClassHierarchy cha;
    
    // The results of the analysis:
    protected Map<TransferSite, Set<PointerKey>> liveVariables;


    public TransferSitesLiveAnalysis(CapsuleTemplate template,
                                     LocalLiveAnalysisFactory localLiveAnalysisFactory,
                                     TransferSitesAnalysis transferSitesAnalysis,
                                     CallGraphAnalysis cfa,
                                     IClassHierarchy cha)
    {
        this.capsuleTemplate = template;
        this.localLiveAnalysisFactory = localLiveAnalysisFactory;
        this.transferSitesAnalysis = transferSitesAnalysis;
        this.cfa = cfa;
        this.cha = cha;
    }


    public void perform()
    {
        transferSitesAnalysis.perform();

        for (CGNode node : transferSitesAnalysis.getReachingNodes())
        {
            Set<TransferSite> relevantSites = transferSitesAnalysis.getRelevantSites(node);
            LocalLiveAnalysis localAnalysis = localLiveAnalysisFactory.lookupOrMake(node);
            localAnalysis.perform();
            for (TransferSite site : relevantSites) {
                liveVariables.put(site, getPointerKeysAfter(site));
            }
        }
    }


    protected void addPointerKeysAfter(TransferSite transferSite)
    {
        CGNode node = transferSite.getNode();
        assert cfa.getCallGraph().containsNode(node);

        LocalLiveAnalysis localLiveAnalysis = localLiveAnalysisFactory.lookupOrMake(node);
        localLiveAnalysis.perform();

        IR nodeIR = node.getIR();
        ISSABasicBlock block = nodeIR.getBasicBlockForInstruction(transferSite.getInstruction());
        liveVariables.put(transferSite, localLiveAnalysis.getPointerKeysAfter(block));
    }


    /**
     * @param transferSite A transfer site defined w.r.t. the factory's call graph analysis.
     * 
     * @return The set of pointer keys into the heap model of the factory's call graph analysis
     *         which the analysis found to be live after the program point of the given transfer
     *         site.
     */
    public Set<PointerKey> getPointerKeysAfter(TransferSite transferSite)
    {
        return liveVariables.get(transferSite);
    }
}
