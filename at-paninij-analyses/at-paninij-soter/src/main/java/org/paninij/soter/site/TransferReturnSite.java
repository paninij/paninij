package org.paninij.soter.site;

import static java.text.MessageFormat.format;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.util.intset.IntSet;

public class TransferReturnSite extends ReturnSite implements ITransferSite
{
    protected final SSAReturnInstruction returnInstr;
    protected final IntSet transfers;
    
    public TransferReturnSite(CGNode node, SSAReturnInstruction returnInstr, IntSet transfers)
    {
        super(node, returnInstr);
        assert returnInstr != null;
        assert transfers != null;

        this.returnInstr = returnInstr;
        this.transfers = transfers;
    }
    
    @Override
    public IntSet getTransfers()
    {
        return transfers;
    } 
    
    @Override
    public JsonObjectBuilder toJsonBuilder()
    {
        return super.toJsonBuilder().add("transfers", transfers.toString());
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
        if (o instanceof TransferReturnSite == false) {
            return false;
        }

        // Note that the class definition asserts that all fields are non-null by construction.
        TransferReturnSite that = (TransferReturnSite) o;
        return super.equals(that)
            && returnInstr.equals(that.returnInstr)
            && transfers.equals(that.transfers);
    }


}
