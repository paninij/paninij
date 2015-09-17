package org.paninij.soter.site;

import com.ibm.wala.util.intset.IntSet;

public class SiteFactory
{
    public static TransferringSite copyWith(TransferringSite orig, IntSet transfers)
    {
        if (orig instanceof TransferringCallSite)
        {
            TransferringCallSite casted = (TransferringCallSite) orig;
            return new TransferringCallSite(orig.getNode(), casted.getInvokeInstruction(),
                                            casted.getCallSite(), transfers);
        }
        if (orig instanceof TransferringReturnSite)
        {
            TransferringReturnSite casted = (TransferringReturnSite) orig;
            return new TransferringReturnSite(orig.getNode(), casted.getReturnInstruction(),
                                              transfers);
        }

        String msg = "Failed to copy the transfer site because its type is unknown: " + orig;
        throw new IllegalArgumentException(msg);
    }
}
