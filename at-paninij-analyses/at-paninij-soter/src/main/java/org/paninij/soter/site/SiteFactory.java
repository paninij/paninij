package org.paninij.soter.site;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.util.intset.IntSet;

public class SiteFactory
{
    public static TransferringSite copyWith(TransferringSite orig, IntSet transfers)
    {
        if (orig instanceof TransferringCallSite)
        {
            return new TransferringCallSite(orig.getNode(),
                                        ((TransferringCallSite) orig).getInvokeInstruction(),
                                        transfers);
        }
        if (orig instanceof TransferringReturnSite)
        {
            return new TransferringReturnSite(orig.getNode(),
                                          ((TransferringReturnSite) orig).getReturnInstruction(),
                                          transfers);
        }

        String msg = "Failed to copy the transfer site because its type is unknown: " + orig;
        throw new IllegalArgumentException(msg);
    }


    /**
     * @param node       The call graph node in which this transfer
     * @param transfers  A set of value numbers (from the SSA IR of `node`) which are transferred at
     *                   this transfer site. If there are no transfers, then that
     *                   should be represented by passing `null`.
     * @param instr      The instruction (from the SSA IR of `node`) which performs the transfer.
     */
    public static TransferringSite make(CGNode node, SSAInstruction instr, IntSet transfers)
    {
        switch (TransferringSite.Kind.fromSSAInstruction(instr)) {
        case INVOKE:
            return new TransferringCallSite(node, (SSAAbstractInvokeInstruction) instr, transfers);
        case RETURN:
            return new TransferringReturnSite(node, (SSAReturnInstruction) instr, transfers);
        default:
            String msg = "Cannot make a transfer site because `transferInstr` kind is unknown.";
            throw new RuntimeException(msg);
        }
    }
}
