package org.paninij.soter.transfer;

import static org.paninij.soter.util.PaniniModel.isProcedure;
import static org.paninij.soter.util.PaniniModel.isRemoteProcedure;
import static org.paninij.soter.util.PaniniModel.isKnownSafeTypeForTransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.paninij.runtime.util.IdentitySet;
import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.site.TransferringSite;
import org.paninij.soter.site.TransferringCallSite;
import org.paninij.soter.site.AnalysisCallSite;
import org.paninij.soter.site.AnalysisSite;
import org.paninij.soter.site.TransferringReturnSite;
import org.paninij.soter.util.AnalysisJsonResultsCreator;
import org.paninij.soter.util.LoggingAnalysis;
import org.paninij.soter.util.Sets;
import org.paninij.soter.util.SoterUtil;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.intset.BitVectorIntSet;
import com.ibm.wala.util.intset.MutableIntSet;


public class SiteAnalysis extends LoggingAnalysis
{
    protected final CapsuleTemplate template;
    protected final CallGraphAnalysis cga;
    protected final IClassHierarchy cha;

    /**
     * A map whose domain is the set of transferring nodes. It maps from a given node to the set
     * of all transfer sites which were found to include some potentially unsafe transfers.
     */
    protected final Map<CGNode, Set<TransferringSite>> transferringSitesMap;
    
    /**
     * The set of nodes which, by some finite sequence of calls in the CGA, reach a transferring
     * call graph node.
     */
    protected IdentitySet<CGNode> reachingNodes;
    
    /**
     * A map whose domain is the set of reaching nodes. It maps from a given node to the set of
     * "relevant" analysis sites defined within this node. These are the sites at which local live
     * analysis information will be collected. There are three kinds of relevant sites:
     * 
     *  1. an transferring call site defined within the node,
     *  2. a transferring return site defined within the node, or
     *  3. a call site defined within the node which was found by the CGA to possibly target a
     *     reaching node.
     */
    protected final Map<CGNode, Set<AnalysisSite>> relevantSitesMap;
    
    /**
     * A map whose domain is the set of reaching nodes. It maps from a given node to the set of
     * all call site references which were found by the CGA to possibly target that node.
     */
    protected final Map<CGNode, Set<AnalysisCallSite>> relevantCallersMap;
    
    
    protected final JsonResultsCreator jsonCreator;
    

    // TODO: Refactor this so that it uses dependency injection for selecting whether a particular
    // transfer is known to be safe.
    public SiteAnalysis(CapsuleTemplate template, CallGraphAnalysis cga, IClassHierarchy cha)
    {
        this.template = template;
        this.cga = cga;
        this.cha = cha;
        
        transferringSitesMap = new HashMap<CGNode, Set<TransferringSite>>();
        relevantSitesMap = new HashMap<CGNode, Set<AnalysisSite>>();
        relevantCallersMap = new HashMap<CGNode, Set<AnalysisCallSite>>();
        
        jsonCreator = new JsonResultsCreator();
    }
    
    @Override
    public void performAnalysis()
    {
        buildTransferSitesMap();
        buildReachingNodes();
        buildCalledByMap();
        buildRelevantSitesMap();
    }
    
    protected void buildTransferSitesMap()
    {
        for (CGNode node : cga.getCallGraph())
        {
            // Only add transferring sites from nodes whose methods are declared directly on the
            // capsule template. Ignore any others. This is done because transfer points can only
            // be defined within the capsule template itself.
            if (template.getTemplateClass().equals(node.getMethod().getDeclaringClass())) {
                findTransferSites(node);
            }
        }
    }
    
    protected void buildReachingNodes()
    {
        Set<CGNode> transferringNodes = transferringSitesMap.keySet();
        reachingNodes = SoterUtil.makeCalledByClosure(transferringNodes, cga.getCallGraph());
    }
    

    protected void buildCalledByMap()
    {
        // For each reaching node, add any call sites which might target this node.
        CallGraph cg = cga.getCallGraph();
        for (CGNode callee : reachingNodes)
        {
            Set<AnalysisCallSite> callers = new HashSet<AnalysisCallSite>();

            Iterator<CGNode> predsIter = cg.getPredNodes(callee);
            while (predsIter.hasNext())
            {
                CGNode caller = predsIter.next();
                Iterator<CallSiteReference> callSiteIter = cg.getPossibleSites(caller, callee);
                while (callSiteIter.hasNext()) {
                    callers.add(AnalysisCallSite.make(caller, callSiteIter.next()));
                }
            }

            relevantCallersMap.put(callee, callers);
        }
    }
    
    protected void findTransferSites(CGNode node)
    {
        IMethod method = node.getMethod();

        if (shouldCheckForReturnTransfers(method))
        {
            for (SSAInstruction instr : node.getIR().getInstructions())
            {
                if (instr instanceof SSAReturnInstruction) {
                    foundTransferringSite(node, (SSAReturnInstruction) instr);
                }
                if (instr instanceof SSAAbstractInvokeInstruction) {
                    foundTransferringSite(node, (SSAAbstractInvokeInstruction) instr);
                }
            }
        }
        else
        {
            for (SSAInstruction instr : node.getIR().getInstructions())
            {
                if (instr instanceof SSAAbstractInvokeInstruction) {
                    foundTransferringSite(node, (SSAAbstractInvokeInstruction) instr);
                }
            }
        }
    }
    
    protected boolean shouldCheckForReturnTransfers(IMethod method)
    {
        return isProcedure(method) && !isKnownSafeTypeForTransfer(method.getReturnType());
    }
     
    protected void foundTransferringSite(CGNode node, SSAReturnInstruction returnInstr)
    {
        // A return instruction always has one transfer.
        MutableIntSet transfers = new BitVectorIntSet();
        int returnValueNumber = returnInstr.getUse(0);
        transfers.add(returnValueNumber);
        addTransferringSite(node, new TransferringReturnSite(node, returnInstr, transfers));
    }

    /**
     * Adds a transfer site with every transfer which is not known to be safe. If all of the
     * transfers at a transfer site are known to be safe, then no transfer point is added.
     */
    protected void foundTransferringSite(CGNode node, SSAAbstractInvokeInstruction invokeInstr)
    {
        // Return static methods and dispatch methods with only the receiver argument.
        if (invokeInstr.isStatic() || invokeInstr.getNumberOfParameters() == 1)
            return;

        // Ignore any method which is not a procedure invocation on a remote capsule instance.
        IMethod targetMethod = cha.resolveMethod(invokeInstr.getDeclaredTarget());
        if (! isRemoteProcedure(targetMethod))
            return;
        
        MutableIntSet transfers = new BitVectorIntSet();
        
        for (int idx = 1; idx < targetMethod.getNumberOfParameters(); idx++)
        {
            TypeReference paramType = targetMethod.getParameterType(idx);
            if (! isKnownSafeTypeForTransfer(paramType)) {
                transfers.add(invokeInstr.getUse(idx));
            }
        }
        
        if (! transfers.isEmpty()) {
            addTransferringSite(node, TransferringCallSite.make(node, invokeInstr, transfers));
        }
    }
    
    protected void addTransferringSite(CGNode node, TransferringSite transferSite)
    {
        assert transferSite.getTransfers().isEmpty() == false;

        Set<TransferringSite> sites = transferringSitesMap.get(node);
        if (sites == null) {
            // This is the first transfer site from this `CGNode` to be added to `transferSitesMap`.
            sites = new HashSet<TransferringSite>();
            transferringSitesMap.put(node, sites);
        }
        sites.add(transferSite);
    }

    protected void buildRelevantSitesMap()
    {
        // A call site is considered relevant if it is transferring or if one of the call site's
        // possible call graph targets is in `reaching`.
        for (CGNode node: reachingNodes)
        {
            // Create a new set to hold relevant analysis sites defined within `node`. Initially add
            // all transferring sites defined within `node`.
            Set<AnalysisSite> relevantSites = new HashSet<AnalysisSite>();
            Set<TransferringSite> transferringSites = transferringSitesMap.get(node);
            List<CallSiteReference> alreadyAddedSites = new ArrayList<CallSiteReference>();
            if (transferringSites != null)
            {
                // Needs `null` check because not all reaching nodes include transferring sites.
                relevantSites.addAll(transferringSites);

                // Add all call sites which perform transfers to a list so that these transfer sites
                // are not re-added as analysis sites below. (Note that we need not worry about
                // re-adding a `ReturnTransferSite`, since we are not adding any returns sites
                // below.)
                for (TransferringSite ts: transferringSites)
                {
                    if (ts instanceof TransferringCallSite) {
                        alreadyAddedSites.add(((TransferringCallSite) ts).getCallSite());
                    }
                }
            }
            
       
            Iterator<CallSiteReference> callSiteIter = node.iterateCallSites();
            while(callSiteIter.hasNext())
            {
                // Mark a call site as relevant if it has not already been added an may (according
                // to the call graph analysis) call a reaching node.
                CallSiteReference callSite = callSiteIter.next();
                if (alreadyAddedSites.contains(callSite)) {
                    continue;
                }

                for (CGNode targetNode : cga.getCallGraph().getPossibleTargets(node, callSite))
                {
                    if (reachingNodes.contains(targetNode))
                    {
                        AnalysisSite rs = AnalysisCallSite.make(node, callSite);
                        relevantSites.add(rs);
                        break;
                    }
                }
            }
        
            relevantSitesMap.put(node, relevantSites);
        }
    }
    
    
    /**
     * @param node    A "reaching" call graph node.
     * 
     * @return Transferring sites defined within the given call graph node.
     */
    public Set<TransferringSite> getTransferringSites(CGNode node)
    {
        assert hasBeenPerformed;
        assert reachingNodes.contains(node);
        return transferringSitesMap.get(node);
    }

    /**
     * @return Call graph nodes which include some kind of transfer site.
     */
    public Set<CGNode> getTransferringNodes()
    {
        assert hasBeenPerformed;
        return transferringSitesMap.keySet();
    }
    
    /**
     * @param  node    A "reaching" call graph node.
     * 
     * @return Relevant call sites which may (according to the CGA) target the given node.
     */
    public Set<AnalysisCallSite> getRelevantCallers(CGNode node)
    {
        assert hasBeenPerformed;
        assert reachingNodes.contains(node);
        Set<AnalysisCallSite> retVal = relevantCallersMap.get(node);
        return (retVal == null) ? new HashSet<AnalysisCallSite>() : retVal;
    }
    
    /**
     * @see SiteAnalysis.relevantSitesMap
     */
    public Set<AnalysisSite> getRelevantSites(CGNode node)
    {
        assert hasBeenPerformed;
        return relevantSitesMap.get(node);
    }
    
    /**
     * @return The set of nodes which, by some finite sequence of calls in the `cga`, reach a
     *         transferring call graph node. (See `SoterUtil.makeCalledByClosure()`.)
     */
    public IdentitySet<CGNode> getReachingNodes()
    {
        assert hasBeenPerformed;
        return reachingNodes;
    }

    @Override
    public JsonObject getJsonResults()
    {
        assert hasBeenPerformed;
        return jsonCreator.toJson();
    }

    @Override
    public String getJsonResultsString()
    {
        assert hasBeenPerformed;
        return jsonCreator.toJsonString();
    }


    @Override
    protected String getJsonResultsLogFileName()
    {
        return template.getQualifiedName().replace('/', '.') + ".json";
    }
    
    private class JsonResultsCreator extends AnalysisJsonResultsCreator
    {
        @Override
        public JsonObject toJson()
        {
            assert hasBeenPerformed;
            
            if (json != null) {
                return json;
            }
            
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("capsuleTemplate", template.getQualifiedName());
            builder.add("transferringSites", toJsonBuilder(transferringSitesMap));
            builder.add("reachingNodes", toJsonBuilder(reachingNodes));
            builder.add("relevantSites", toJsonBuilder(relevantSitesMap));
            builder.add("calledBy", toJsonBuilder(relevantCallersMap));
            
            json = builder.build();
            return json;
        }
        
        @Override
        public CallGraph getCallGraph()
        {
            return cga.getCallGraph();
        }
    }
    
    
    @Override
    public boolean checkPostConditions()
    {
        return Sets.isWellDefinedOverDomain(relevantSitesMap, reachingNodes)
            && Sets.isWellDefinedOverDomain(relevantCallersMap, reachingNodes);
    }

}
