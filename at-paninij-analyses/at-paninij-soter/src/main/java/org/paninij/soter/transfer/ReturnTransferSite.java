package org.paninij.soter.transfer;

import static java.text.MessageFormat.format;

import javax.json.Json;
import javax.json.JsonObject;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.util.intset.IntSet;

public class ReturnTransferSite extends TransferSite
{
    public ReturnTransferSite(CGNode node, IntSet transfers, SSAInstruction transferInstr)
    {
        super(node, transfers, transferInstr);
    }

    @Override
    public TransferSite.Kind getKind()
    {
        return Kind.RETURN;
    }
    
    @Override
    public JsonObject toJson()
    {
        SSAReturnInstruction retInstr = (SSAReturnInstruction) transferInstr;

        return Json.createObjectBuilder()
                   .add("kind", "INVOKE")
                   .add("transfers", transfers.toString())
                   .add("instruction", transferInstr.toString())
                   .add("iindex", retInstr.iindex)
                   .add("method", node.getMethod().getSignature())
                   .add("context", node.getContext().toString())
                   .build();
    }
    
    public String toString()
    {
        String fmt = "ReturnTransferSite(node = {0}, SSAInstruction = {1}, transfers = {2})";
        return format(fmt, node, transferInstr, transfers);
    }
 
}
