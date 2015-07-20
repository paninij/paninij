package org.paninij.soter.live;

import java.util.Map;
import java.util.Set;

import org.paninij.runtime.util.IdentitySet;
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
    final protected CapsuleTemplate capsuleTemplate;
    final protected CallGraphAnalysis cfa;
    final protected IClassHierarchy cha;

    final protected LocalLiveAnalysisFactory localLiveAnalysisFactory;
    final protected TransferSitesAnalysis transferSitesAnalysis;
    
    protected Set<CGNode> relevantNodes;
    protected IdentitySet<CGNode> reachingNodes;
    protected Map<TransferSite, Set<PointerKey>> liveVariables;

    public TransferSitesLiveAnalysis(CapsuleTemplate template, CallGraphAnalysis cfa, IClassHierarchy cha)
    {
        this.capsuleTemplate = template;
        this.cfa = cfa;
        this.cha = cha;

        localLiveAnalysisFactory = new LocalLiveAnalysisFactory(cfa);
        transferSitesAnalysis = new TransferSitesAnalysis(template, cfa, cha);
    }
    
    public void perform()
    {
        transferSitesAnalysis.perform();
        reachingNodes = transferSitesAnalysis.getReachingNodes();        
        relevantNodes = transferSitesAnalysis.getRelevantNodes();

        for (CGNode node : reachingNodes)
        {
            Set<TransferSite> relevantSites = transferSitesAnalysis.getRelevantSites(node);
            LocalLiveAnalysis localAnalysis = localLiveAnalysisFactory.lookupOrMake(node);
            localAnalysis.perform();
            for (TransferSite site : relevantSites) {
                liveVariables.put(site, getPointerKeysAfter(site));
            }
        }
        
        // Free all sub-analyses, because all relevant information from them has already been saved.
        transferSitesAnalysis.resetAnalysis();
        localLiveAnalysisFactory.resetAnalysisCache();
    }

    /**
     * @param transferSite A transfer site defined w.r.t. the factory's call graph analysis.
     * @return The set of pointer keys into the heap model of the factory's call graph analysis
     *         which the analysis found to be live at the "in" program point of the given transfer
     *         site.
     */
    protected Set<PointerKey> getPointerKeysAfter(TransferSite transferSite)
    {
        CGNode node = transferSite.getNode();
        assert cfa.getCallGraph().containsNode(node);

        LocalLiveAnalysis localLiveAnalysis = localLiveAnalysisFactory.lookupOrMake(node);
        localLiveAnalysis.perform();

        IR nodeIR = node.getIR();
        ISSABasicBlock block = nodeIR.getBasicBlockForInstruction(transferSite.getInstruction());
        return localLiveAnalysis.getPointerKeysAfter(block);
    }
}
