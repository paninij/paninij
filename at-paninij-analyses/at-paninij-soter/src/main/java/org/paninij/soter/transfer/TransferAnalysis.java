package org.paninij.soter.transfer;

import static org.paninij.soter.util.PaniniModel.isProcedure;
import static org.paninij.soter.util.PaniniModel.isRemoteProcedure;
import static org.paninij.soter.util.PaniniModel.isKnownSafeTypeForTransfer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.paninij.runtime.util.IdentitySet;
import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.util.Analysis;
import org.paninij.soter.util.SoterUtil;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.intset.BitVectorIntSet;
import com.ibm.wala.util.intset.MutableIntSet;

public class TransferAnalysis extends Analysis
{
    protected final CapsuleTemplate template;
    protected final CallGraphAnalysis cga;
    protected final IClassHierarchy cha;

    /**
     * A map from some call graph node to the set of the node's transfer sites which were found to
     * include some potentially unsafe transfers.
     */
    protected Map<CGNode, Set<TransferSite>> transferringSitesMap;
    
    /**
     * The set of nodes which, by some finite sequence of calls in the `cga`, reach a transferring
     * call graph node.
     */
    protected IdentitySet<CGNode> reachingNodes;
    
    /**
     * A map from some call graph node to the set of the node's transfer sites which were not found
     * to include any potentially unsafe transfers, but were found to be considered relevant. Here,
     * "relevant" means that the transfer is an invocation which includes a call graph target to a
     * "reachable" node.
     */
    protected Map<CGNode, Set<TransferSite>> otherRelevantSitesMap;
    
    // TODO: Refactor this so that it uses dependency injection for selecting whether a particular
    // transfer is known to be safe.
    // TODO: Refactor this so that it uses dependency injection for selecting whether a particular
    // transfer is "transferring" and/or "relevant".
    public TransferAnalysis(CapsuleTemplate template, CallGraphAnalysis cga,
                                 IClassHierarchy cha)
    {
        this.template = template;
        this.cga = cga;
        this.cha = cha;

        transferringSitesMap = new HashMap<CGNode, Set<TransferSite>>();
        otherRelevantSitesMap = new HashMap<CGNode, Set<TransferSite>>();
    }
    
    @Override
    public void performAnalysis()
    {
        for (CGNode node : cga.getCallGraph())
        {
            // Only add transfer sites from nodes whose methods are declared directly on the capsule
            // template. Ignore any others. This is done because transfer points can only be defined
            // within the capsule template itself.
            if (template.getTemplateClass().equals(node.getMethod().getDeclaringClass())) {
                findTransferSites(node);
            }
        }
        
        Set<CGNode> transferringNodes = transferringSitesMap.keySet();
        reachingNodes = SoterUtil.makeCalledByClosure(transferringNodes, cga.getCallGraph());

        for (CGNode node : cga.getCallGraph())
        {
            if (transferringNodes.contains(node)) {
                findOtherRelevantSites(node);
            }
        }
    }

    public void findTransferSites(CGNode node)
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
        addTransferringSite(node, new ReturnTransferSite(node, transfers, returnInstr));
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
            addTransferringSite(node, new InvokeTransferSite(node, transfers, invokeInstr));
        }
    }
    
    protected void addTransferringSite(CGNode node, TransferSite transferSite)
    {
        assert transferSite.getTransfers().isEmpty() == false;

        Set<TransferSite> sites = transferringSitesMap.get(node);
        if (sites == null) {
            // This is the first transfer site from this `CGNode` to be added to `transferSitesMap`.
            sites = new HashSet<TransferSite>();
            transferringSitesMap.put(node, sites);
        }
        sites.add(transferSite);
    }

    /**
     * A call site is considered relevant if it is transferring or if one of the call site's
     * possible call graph targets is in `reaching`.
     * 
     * @return The set of all relevant call sites within the given call graph node.
     */
    protected void findOtherRelevantSites(CGNode node)
    {
        Iterator<CallSiteReference> callSiteIter = node.iterateCallSites();
        while(callSiteIter.hasNext())
        {
            CallSiteReference callSite = callSiteIter.next();
            for (CGNode targetNode : cga.getCallGraph().getPossibleTargets(node, callSite))
            {
                if (reachingNodes.contains(targetNode))
                {
                    foundOtherRelevantSite(node, callSite);
                    break;
                }
            }
        }
    }
    
    protected void foundOtherRelevantSite(CGNode node, CallSiteReference callSite)
    {
        SSAAbstractInvokeInstruction[] instrs = node.getIR().getCalls(callSite);
        if (instrs.length != 1) {
            String msg = "The given call site does not have exactly one SSA IR insturction!";
            throw new RuntimeException(msg);
        }
        addOtherRelevantSite(node, new InvokeTransferSite(node, null, instrs[0]));
    }
    
    protected void addOtherRelevantSite(CGNode node, TransferSite transferSite)
    {
        assert transferSite.getTransfers() == null;

        Set<TransferSite> sites = transferringSitesMap.get(node);
        if (sites == null) {
            // This is the first transfer site from this `CGNode` to be added to `transferSitesMap`.
            sites = new HashSet<TransferSite>();
            transferringSitesMap.put(node, sites);
        }
        sites.add(transferSite);
    }

    public Set<TransferSite> getTransferringSites(CGNode node)
    {
        assert hasBeenPerformed;
        return transferringSitesMap.get(node);
    }

    public Set<CGNode> getTransferringNodes()
    {
        assert hasBeenPerformed;
        return transferringSitesMap.keySet();
    }
    
    public Set<CGNode> getRelevantNodes()
    {
        assert hasBeenPerformed;
        Set<CGNode> relevantNodes = new HashSet<CGNode>();
        relevantNodes.addAll(transferringSitesMap.keySet());
        relevantNodes.addAll(otherRelevantSitesMap.keySet());
        return relevantNodes;
    }
    
    public Set<TransferSite> getRelevantSites(CGNode node)
    {
        assert hasBeenPerformed;
        Set<TransferSite> relevantSites = new HashSet<TransferSite>();
        if (transferringSitesMap.containsKey(node)) {
            relevantSites.addAll(transferringSitesMap.get(node));
        }
        if (otherRelevantSitesMap.containsKey(node)) {
            relevantSites.addAll(otherRelevantSitesMap.get(node));
        }
        return relevantSites;
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
}
