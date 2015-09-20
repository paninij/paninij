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
    public int hashCode()
    {
        // Note that the class definition asserts that all fields are non-null by construction.
        final int prime = 31;
        int result = 1;
        result = prime * result + node.hashCode();
        result = prime * result + instr.hashCode();
        result = prime * result + returnInstr.hashCode();
        result = prime * result + transfers.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof TransferringReturnSite == false) {
            return false;
        }

        // Note that the class definition asserts that all fields are non-null by construction.
        TransferringReturnSite that = (TransferringReturnSite) o;
        return super.equals(that)
            && returnInstr.equals(that.returnInstr)
            && transfers.equals(that.transfers);
    } 
}
