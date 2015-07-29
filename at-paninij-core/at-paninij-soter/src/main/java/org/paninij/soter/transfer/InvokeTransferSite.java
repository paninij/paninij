package org.paninij.soter.transfer;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.util.intset.IntSet;

public class InvokeTransferSite extends TransferSite
{
    public InvokeTransferSite(CGNode node, IntSet transfers,
                              SSAAbstractInvokeInstruction transferInstr)
    {
        super(node, transfers, transferInstr);
    }

    @Override
    public boolean isReturnKind()
    {
        return false;
    }

    @Override
    public boolean isInvokeKind()
    {
        return true;
    }
}
