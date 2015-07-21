package org.paninij.soter.transfer;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.util.intset.IntSet;

public class ReturnTransferSite extends TransferSite
{
    public ReturnTransferSite(CGNode node, IntSet transfers, SSAReturnInstruction transferInstr)
    {
        super(node, transfers, transferInstr);
    }

    @Override
    public boolean isReturnKind()
    {
        return true;
    }

    @Override
    public boolean isInvokeKind()
    {
        return false;
    }
}
