package org.paninij.soter.model;

import java.text.MessageFormat;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.util.intset.IntSet;

public class TransferSite
{
    protected CGNode node;
    protected SSAInstruction transferInstr;
    protected IntSet transfers;
    protected Kind kind;
    
    /**
     * @param node          The call graph node in which this transfer
     * @param transferInstr The instruction (from the SSA IR of `node`) which performs the transfer.
     * @param transfers     A set of value numbers (from the SSA IR of `node`) which are
     *                      transferred at this transfer site.
     */
    public TransferSite(CGNode node, SSAInstruction transferInstr, IntSet transfers)
    {
        this.node = node;
        this.transferInstr = transferInstr;
        this.transfers = transfers;
    }
    
    public boolean isReturnKind()
    {
        return kind == Kind.RETURN;
    }
    
    public boolean isInvokeKind()
    {
        return kind == Kind.INVOKE;
    }
    
    public SSAInstruction getInstruction()
    {
        return transferInstr;
    }
    
    public CGNode getNode()
    {
        return node;
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
