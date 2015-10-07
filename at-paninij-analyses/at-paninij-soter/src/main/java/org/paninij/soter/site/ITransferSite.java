package org.paninij.soter.site;

import com.ibm.wala.util.intset.IntSet;

public interface ITransferSite extends ISite
{
    /**
     * @return An IntSet of those SSA IR value numbers which are transfers.
     */
    public IntSet getTransfers();
}
