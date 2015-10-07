package org.paninij.soter.site;

import static java.text.MessageFormat.format;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAReturnInstruction;

public class ReturnSite extends Site implements IReturnSite
{
    protected final SSAReturnInstruction returnInstr;
    
    public ReturnSite(CGNode node, SSAReturnInstruction returnInstr)
    {
        super(node, returnInstr);
        assert returnInstr != null;

        this.returnInstr = returnInstr;
    }

    @Override
    public SSAReturnInstruction getReturnInstruction()
    {
        return returnInstr;
    }

    @Override
    public JsonObjectBuilder toJsonBuilder()
    {
        return Json.createObjectBuilder()
                   .add("type", "TransferringReturnSite")
                   .add("returnInstr", returnInstr.toString())
                   .add("iindex", returnInstr.iindex)
                   .add("method", node.getMethod().getSignature())
                   .add("context", node.getContext().toString())
                   .add("hashCode", hashCode());
    }
    
    @Override
    public String toString()
    {
        String fmt = "ReturnSite(node = {0}, returnInstr = {1})";
        return format(fmt, node, returnInstr);
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        // Note that the class definition asserts that all fields are non-null by construction.
        result = prime * result + node.hashCode();
        result = prime * result + instr.hashCode();
        result = prime * result + returnInstr.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof CallSite == false) {
            return false;
        }

        // Note that the class definition asserts that all fields are non-null by construction.
        ReturnSite that = (ReturnSite) o;
        return super.equals(that) && returnInstr.equals(that.returnInstr);
    }
}
