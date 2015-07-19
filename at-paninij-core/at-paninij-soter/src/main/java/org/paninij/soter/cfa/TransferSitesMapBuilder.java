package org.paninij.soter.cfa;

import static org.paninij.soter.util.PaniniModel.isProcedure;
import static org.paninij.soter.util.PaniniModel.isRemoteProcedure;
import static org.paninij.soter.util.PaniniModel.isKnownSafeTypeForTransfer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.paninij.soter.model.TransferSite;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.intset.BitVectorIntSet;
import com.ibm.wala.util.intset.MutableIntSet;

public class TransferSitesMapBuilder
{
    IClassHierarchy cha;
    protected Map<CGNode, Set<TransferSite>> transferSitesMap;
    
    // TODO: Refactor this so that it uses dependency injection for selecting whether a particular
    // transfer is known to be safe.
    public TransferSitesMapBuilder(IClassHierarchy cha)
    {
        this.cha = cha;
        transferSitesMap = new HashMap<CGNode, Set<TransferSite>>();
    }

    public Map<CGNode, Set<TransferSite>> getTransferSitesMap()
    {
        return transferSitesMap;
    }

    public void addTransferSitesFrom(CGNode node)
    {
        IMethod method = node.getMethod();

        if (isProcedure(method) && !isKnownSafeTypeForTransfer(method.getReturnType()))
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
     
    protected void addTransferSite(CGNode node, SSAReturnInstruction returnInstr)
    {
        // A return instruction always has one transfer.
        MutableIntSet transfers = new BitVectorIntSet();
        int returnValueNumber = returnInstr.getUse(0);
        transfers.add(returnValueNumber);
        addTransferSite(node, new TransferSite(node, returnInstr, transfers));
    }
    
    /**
     * Adds a transfer site with every transfer which is not known to be safe. If all of the
     * transfers at a transfer site are known to be safe, then no transfer point is added.
     */
    protected void maybeAddTransferSite(CGNode node, SSAAbstractInvokeInstruction invokeInstr)
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
}
