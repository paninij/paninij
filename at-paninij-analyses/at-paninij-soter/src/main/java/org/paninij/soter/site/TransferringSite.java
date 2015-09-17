package org.paninij.soter.site;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.util.intset.IntSet;

public abstract class TransferringSite extends AnalysisSite
{
    protected final IntSet transfers;
    
    protected TransferringSite(CGNode node, SSAInstruction instr, IntSet transfers)
    {
        super(node, instr);
        assert transfers == null || ! transfers.isEmpty();

        this.transfers = transfers;
    }
    
    /**
     * @return An IntSet of the SSA IR value numbers which are potentially-unsafe transfers (or
     *         `null` if there are none).
     */
    public IntSet getTransfers()
    {
        return transfers;
    }
}
