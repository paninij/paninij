package org.paninij.soter.site;

import com.ibm.wala.util.intset.IntSet;

public class SiteFactory
{
    public static ITransferSite copyWith(ITransferSite orig, IntSet transfers)
    {
        if (orig instanceof TransferCallSite)
        {
            TransferCallSite casted = (TransferCallSite) orig;
            return new TransferCallSite(orig.getNode(), casted.getInvokeInstruction(),
                                            casted.getCallSite(), transfers);
        }
        if (orig instanceof TransferReturnSite)
        {
            TransferReturnSite casted = (TransferReturnSite) orig;
            return new TransferReturnSite(orig.getNode(), casted.getReturnInstruction(),
                                              transfers);
        }

        String msg = "Failed to copy the transfer site because its type is unknown: " + orig;
        throw new IllegalArgumentException(msg);
    }
}
