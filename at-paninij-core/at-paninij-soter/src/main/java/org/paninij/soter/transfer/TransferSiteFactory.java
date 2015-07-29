package org.paninij.soter.transfer;

import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.util.intset.IntSet;

public class TransferSiteFactory
{
    public static TransferSite copyWith(TransferSite orig, IntSet transfers)
    {
        if (orig instanceof InvokeTransferSite)
        {
            return new InvokeTransferSite(orig.getNode(),
                                          transfers,
                                          (SSAAbstractInvokeInstruction) orig.getInstruction());
        }
        if (orig instanceof ReturnTransferSite)
        {
            return new ReturnTransferSite(orig.getNode(),
                                          transfers,
                                          (SSAReturnInstruction) orig.getInstruction());
        }

        String msg = "Failed to copy the transfer site because its type is unknown: " + orig;
        throw new IllegalArgumentException(msg);
    }
}
