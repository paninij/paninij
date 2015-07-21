package org.paninij.soter.transfer;

import java.text.MessageFormat;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.util.intset.IntSet;

public abstract class TransferSite
{
    protected CGNode node;
    protected IntSet transfers;
    protected SSAInstruction transferInstr;
    protected Kind kind;
    
    /**
     * @param node          The call graph node in which this transfer
     * @param transferInstr The instruction (from the SSA IR of `node`) which performs the transfer.
     * @param transfers     A set of value numbers (from the SSA IR of `node`) which are
     *                      transferred at this transfer site. If there are no transfers, then that
     *                      should be represented by passing `null`.
     */
    public TransferSite(CGNode node, IntSet transfers, SSAInstruction transferInstr)
    {
        assert node != null;
        assert transfers == null || ! transfers.isEmpty();
        assert transferInstr != null;

        this.node = node;
        this.transferInstr = transferInstr;
        this.transfers = transfers;
    }
    
    public abstract boolean isReturnKind();
    
    public abstract boolean isInvokeKind();

    public CGNode getNode()
    {
        return node;
    }
    
    public SSAInstruction getInstruction()
    {
        return transferInstr;
    }
    
    /**
     * @return An IntSet of the SSA IR value numbers which are potentially-unsafe transfers (or
     *         `null` if there are none).
     */
    public IntSet getTransfers()
    {
        return transfers;
    }

    public static enum Kind
    {
        INVOKE,
        RETURN;
        
        public static Kind fromSSAInstruction(SSAInstruction instr)
        {
            if (instr instanceof SSAAbstractInvokeInstruction)
                return INVOKE;
            if (instr instanceof SSAReturnInstruction)
                return RETURN;
            
            String msg = "The given instruction is not a known transfer site kind: " + instr;
            throw new IllegalArgumentException(msg);
        }
    }
    
    @Override
    public String toString()
    {
        String fmt = "TransferSite(node = {0}, SSAInstruction = {1}, transfers = {2})";
        return MessageFormat.format(fmt, node, transferInstr, transfers);
    }
}
