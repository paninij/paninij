package org.paninij.soter.site;

import static java.text.MessageFormat.format;

import javax.json.Json;
import javax.json.JsonObject;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.intset.IntSet;

public class TransferringCallSite extends TransferringSite implements CallSite
{
    protected final SSAAbstractInvokeInstruction invokeInstr;
    
    public TransferringCallSite(CGNode node, SSAAbstractInvokeInstruction invokeInstr, IntSet transfers)
    {
        super(node, invokeInstr, transfers);
        this.invokeInstr = invokeInstr;
    }

    @Override
    public JsonObject toJson()
    {
        return Json.createObjectBuilder()
                   .add("kind", "INVOKE")
                   .add("sourceMethod", node.getMethod().getSignature())
                   .add("programCounter", invokeInstr.getProgramCounter())
                   .add("targetMethod", invokeInstr.getDeclaredTarget().getSignature())
                   .add("transfers", transfers.toString())
                   .add("invokeInstruction", invokeInstr.toString())
                   .add("iindex", invokeInstr.iindex)
                   .add("method", node.getMethod().getSignature())
                   .add("context", node.getContext().toString())
                   .build();
    }

    @Override
    public String toString()
    {
        String fmt = "InvokeTransferSite(node = {0}, SSAAbstractInvokeInstruction = {1}, transfers = {2})";
        return format(fmt, node, invokeInstr, transfers);
    }

    @Override
    public CallSiteReference getCallSite()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SSAAbstractInvokeInstruction getInvokeInstruction()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
