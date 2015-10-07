package org.paninij.soter.site;

import static java.text.MessageFormat.format;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.util.intset.IntSet;

public class TransferCallSite extends CallSite implements ITransferSite
{
    protected final IntSet transfers;
    
    public static TransferCallSite make(CGNode node, SSAAbstractInvokeInstruction invokeInstr,
                                            IntSet transfers)
    {
        return new TransferCallSite(node, invokeInstr, invokeInstr.getCallSite(), transfers);
    }
    
    public TransferCallSite(CGNode node, SSAAbstractInvokeInstruction invokeInstr,
                                CallSiteReference callSite, IntSet transfers)
    {
        super(node, invokeInstr, callSite);
        assert callSite != null;

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
    public CallSiteReference getCallSite() {
        return callSite;
    }

    @Override
    public SSAAbstractInvokeInstruction getInvokeInstruction()
    {
        return invokeInstr;
    }
    
    @Override
    public String toString()
    {
        String fmt = "TransferringCallSite(node = {0}, invokeInstr = {1}, callSite = {2}, transfers = {3})";
        return format(fmt, node, invokeInstr, callSite, transfers);
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + transfers.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof TransferCallSite == false) {
            return false;
        }

        // Note that the class definition asserts that all fields are non-null by construction.
        TransferCallSite that = (TransferCallSite) o;
        return super.equals(that)
            && transfers.equals(that.transfers);
    }
}
