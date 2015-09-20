package org.paninij.soter.site;

import static java.text.MessageFormat.format;

import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonObject;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.util.intset.IntSet;

public class TransferringCallSite extends TransferringSite implements CallSite
{
    protected final SSAAbstractInvokeInstruction invokeInstr;
    protected final CallSiteReference callSite;
    
    public static TransferringCallSite make(CGNode node,
                                            SSAAbstractInvokeInstruction invokeInstr,
                                            IntSet transfers)
    {
        return new TransferringCallSite(node, invokeInstr, invokeInstr.getCallSite(), transfers);
    }
    
    public TransferringCallSite(CGNode node, SSAAbstractInvokeInstruction invokeInstr,
                                CallSiteReference callSite, IntSet transfers)
    {
        super(node, invokeInstr, transfers);
        assert callSite != null;

        this.invokeInstr = invokeInstr;
        this.callSite = callSite;
    }

    @Override
    public JsonObject toJson()
    {
        return Json.createObjectBuilder()
                   .add("type", "TransferringCallSite")
                   .add("sourceMethod", node.getMethod().getSignature())
                   .add("programCounter", invokeInstr.getProgramCounter())
                   .add("targetMethod", invokeInstr.getDeclaredTarget().getSignature())
                   .add("invokeInstr", invokeInstr.toString())
                   .add("callSite", callSite.toString())
                   .add("transfers", transfers.toString())
                   .add("iindex", invokeInstr.iindex)
                   .add("method", node.getMethod().getSignature())
                   .add("context", node.getContext().toString())
                   .add("hashCode", hashCode())
                   .build();
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
    public boolean equals(Object o)
    {
        if (o instanceof TransferringCallSite == false) {
            return false;
        }

        // Note that the class definition asserts that all fields are non-null by construction.
        TransferringCallSite that = (TransferringCallSite) o;
        return node.equals(that.node)
            && instr.equals(that.instr)
            && invokeInstr.equals(that.invokeInstr)
            && callSite.equals(that.callSite)
            && transfers.equals(that.transfers);
    }
}
