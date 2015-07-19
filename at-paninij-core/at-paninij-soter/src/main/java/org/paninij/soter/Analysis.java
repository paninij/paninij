package org.paninij.soter;

import static org.paninij.soter.util.PaniniModel.isProcedure;
import static org.paninij.soter.util.PaniniModel.isRemoteProcedure;
import static org.paninij.soter.util.PaniniModel.isKnownSafeTypeForTransfer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.paninij.soter.cfa.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.model.TransferSite;

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

public class Analysis
{
    protected CapsuleTemplate capsule;
    protected CallGraphAnalysis cfa;
    protected IClassHierarchy cha;

    protected Map<CGNode, Set<TransferSite>> transferSitesMap;

    public Analysis(CapsuleTemplate capsule, CallGraphAnalysis cfa, IClassHierarchy cha)
    {
        this.capsule = capsule;
        this.cfa = cfa;
        this.cha = cha;
        
        transferSitesMap = new HashMap<CGNode, Set<TransferSite>>();
    }
    
    public void perform()
    {
        buildTransferSites();
    }

    /**
     * Builds `transferSites` and the associated instance variable, `transferingNodes`.
     */
    protected void buildTransferSites()
    {
        for (CGNode node : cfa.getCallGraph())
        {
            IMethod method = node.getMethod();

            // Ignore any node whose method is not directly defined as part of the capsule template,
            // because a capsule's transfer points can only be defined within the capsule template.
            if (! capsule.template.equals(method.getDeclaringClass())) {
                continue;
            }
            
            if (mightHaveReturnTransfers(method))
            {
                for (SSAInstruction instr : node.getIR().getInstructions())
                {
                    if (instr instanceof SSAReturnInstruction) {
                        addTransferSite(node, (SSAReturnInstruction) instr);
                    }
                    if (instr instanceof SSAAbstractInvokeInstruction) {
                        maybeAddTransferSite(node, (SSAAbstractInvokeInstruction) instr);
                    }
                }
            }
            else
            {
                for (SSAInstruction instr : node.getIR().getInstructions())
                {
                    if (instr instanceof SSAAbstractInvokeInstruction) {
                        maybeAddTransferSite(node, (SSAAbstractInvokeInstruction) instr);
                    }
                }
            }
        }
    }
    
    private boolean mightHaveReturnTransfers(IMethod method)
    {
        TypeReference returnType = method.getReturnType();
        return isProcedure(method) && (returnType.isArrayType() || returnType.isReferenceType());
    }
    
    protected void addTransferSite(CGNode node, SSAReturnInstruction returnInstr)
    {
        // There is always only one transfer.
        MutableIntSet transfers = new BitVectorIntSet();
        int returnValueNumber = returnInstr.getUse(0);
        transfers.add(returnValueNumber);
        addTransferSite(node, new TransferSite(node, returnInstr, transfers));
    }
    
    protected void maybeAddTransferSite(CGNode node, SSAAbstractInvokeInstruction invokeInstr)
    {
        // Return static methods and dispatch methods with only the receiver argument.
        if (invokeInstr.isStatic() || invokeInstr.getNumberOfParameters() == 1)
            return;

        // Ignore any method which is not a procedure invocation on a remote capsule instance.
        IMethod method = cha.resolveMethod(invokeInstr.getDeclaredTarget());
        if (! isRemoteProcedure(method))
            return;
        
        MutableIntSet transfers = new BitVectorIntSet();
        
        for (int idx = 1; idx < method.getNumberOfParameters(); idx++)
        {
            TypeReference paramType = method.getParameterType(idx);
            if (isKnownSafeTypeForTransfer(paramType)) {
                transfers.add(invokeInstr.getUse(idx));
            }
        }
        
        if (! transfers.isEmpty())
        {
            addTransferSite(node, new TransferSite(node, invokeInstr, transfers));
        }
    }
    
    protected void addTransferSite(CGNode node, TransferSite transferSite)
    {
        Set<TransferSite> sites = transferSitesMap.get(node);
        if (sites == null) {
            // This is the first transfer site from this `CGNode` to be added to `transferSitesMap`.
            sites = new HashSet<TransferSite>();
            transferSitesMap.put(node, sites);
        }
        sites.add(transferSite);
    }
   
    /**
     * @return The zero-one CFA call graph starting from the capsule being analyzed.
     */
    public CallGraph getCallGraph()
    {
        return cfa.getCallGraph();
    }
    
    /**
     * @return The capsule being analyzed.
     */
    public CapsuleTemplate getCapsule()
    {
        return capsule;
    }

    /**
     * @return The set of nodes which were found in the analysis to have a transfer point.
     */
    public Set<CGNode> getTransferringNodes()
    {
        return transferSitesMap.keySet();
    }
    
    /**
     * @return The set of transfer sites found within the given call graph node or `null` if there
     *         are no sites associated with this node.
     */
    public Set<TransferSite> getTransferSites(CGNode node)
    {
        return transferSitesMap.get(node);
    }
 }
