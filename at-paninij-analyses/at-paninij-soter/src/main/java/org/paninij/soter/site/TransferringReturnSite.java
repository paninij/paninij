package org.paninij.soter.site;

import static java.text.MessageFormat.format;

import javax.json.Json;
import javax.json.JsonObject;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.util.intset.IntSet;

public class TransferringReturnSite extends TransferringSite
{
    protected final SSAReturnInstruction returnInstr;
    
    public TransferringReturnSite(CGNode node, SSAReturnInstruction returnInstr, IntSet transfers)
    {
        super(node, returnInstr, transfers);
        this.returnInstr = returnInstr;
    }
    
    public SSAReturnInstruction getReturnInstruction() {
        return returnInstr;
    }

    @Override
    public JsonObject toJson()
    {
        return Json.createObjectBuilder()
                   .add("type", "TransferringReturnSite")
                   .add("transfers", transfers.toString())
                   .add("returnInstr", returnInstr.toString())
                   .add("iindex", returnInstr.iindex)
                   .add("method", node.getMethod().getSignature())
                   .add("context", node.getContext().toString())
                   .add("hashCode", hashCode())
                   .build();
    }
    
    @Override
    public String toString()
    {
        String fmt = "TransferringReturnSite(node = {0}, returnInstr = {1}, transfers = {2})";
        return format(fmt, node, returnInstr, transfers);
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof TransferringCallSite == false) {
            return false;
        }

        // Note that the class definition asserts that all fields are non-null by construction.
        TransferringCallSite that = (TransferringCallSite) o;
        return node.equals(that.node)
            && instr.equals(that.instr)
            && returnInstr.equals(that.invokeInstr)
            && transfers.equals(that.transfers);
    } 
}
