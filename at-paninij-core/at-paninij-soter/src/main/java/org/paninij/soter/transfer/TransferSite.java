package org.paninij.soter.transfer;

import static java.text.MessageFormat.format;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.util.intset.IntSet;

public abstract class TransferSite
{
    protected final CGNode node;
    protected final IntSet transfers;
    protected final SSAInstruction transferInstr;
    protected final Kind kind;
    
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
        this.kind = Kind.fromSSAInstruction(transferInstr);
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
    
    public String infoString()
    {
        switch (kind) {
        case INVOKE:
            SSAAbstractInvokeInstruction invoke = (SSAAbstractInvokeInstruction) transferInstr;
            return format("TransferSite(kind = INVOKE, iindex = {0}, target = {1}, programCounter = {2}, transfers = {3})",
                          invoke.iindex,
                          invoke.getDeclaredTarget().getSignature(),
                          invoke.getProgramCounter(),
                          transfers);
        case RETURN:
            SSAReturnInstruction ret = (SSAReturnInstruction) transferInstr;
            return format("TransferSite(kind = RETURN, iindex = {0}, transfers = {1})",
                          ret.iindex,
                          transfers);
        default:
            throw new RuntimeException("Unknown transfer site kind.");
        }
    }
    
    public String debugString()
    {
        String fmt = "TransferSite(node = {0}, SSAInstruction = {1}, transfers = {2})";
        return format(fmt, node, transferInstr, transfers);
    }
    
    @Override
    public String toString()
    {
        return debugString();
    }
}
