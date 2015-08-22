package org.paninij.soter.transfer;

import static java.text.MessageFormat.format;

import javax.json.Json;
import javax.json.JsonObject;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.intset.IntSet;

public class InvokeTransferSite extends TransferSite
{
    public InvokeTransferSite(CGNode node, IntSet transfers, SSAInstruction transferInstr)
    {
        super(node, transfers, transferInstr);
    }

    @Override
    public Kind getKind()
    {
        return Kind.INVOKE;
    }

    @Override
    public JsonObject toJson()
    {
        SSAAbstractInvokeInstruction invokeInstr = (SSAAbstractInvokeInstruction) transferInstr;

        return Json.createObjectBuilder()
                   .add("kind", "INVOKE")
                   .add("sourceMethod", node.getMethod().getSignature())
                   .add("programCounter", invokeInstr.getProgramCounter())
                   .add("targetMethod", invokeInstr.getDeclaredTarget().getSignature())
                   .add("transfers", transfers.toString())
                   .add("instruction", transferInstr.toString())
                   .add("iindex", invokeInstr.iindex)
                   .add("method", node.getMethod().getSignature())
                   .add("context", node.getContext().toString())
                   .build();
    }

    @Override
    public String toString()
    {
        String fmt = "InvokeTransferSite(node = {0}, SSAInstruction = {1}, transfers = {2})";
        return format(fmt, node, transferInstr, transfers);
    }
}
