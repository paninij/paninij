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
                   .add("kind", "INVOKE")
                   .add("transfers", transfers.toString())
                   .add("instruction", returnInstr.toString())
                   .add("iindex", returnInstr.iindex)
                   .add("method", node.getMethod().getSignature())
                   .add("context", node.getContext().toString())
                   .build();
    }
    
    @Override
    public String toString()
    {
        String fmt = "ReturnTransferSite(node = {0}, SSAReturnInstruction = {1}, transfers = {2})";
        return format(fmt, node, returnInstr, transfers);
    }
 
}
