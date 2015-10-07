package org.paninij.soter.site;

import static java.text.MessageFormat.format;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;


public abstract class Site implements ISite
{
    protected final CGNode node;
    protected final SSAInstruction instr;
    
    protected Site(CGNode node, SSAInstruction instr)
    {
        assert node != null;
        assert instr != null;

        this.node = node;
        this.instr = instr;
    }

    @Override
    public SSAInstruction getInstruction() {
        return instr;
    }

    @Override
    public CGNode getNode() {
        return node;
    }

    public JsonObject toJson() {
        return toJsonBuilder().build();
    }
    
    public abstract JsonObjectBuilder toJsonBuilder();
    
    @Override
    public String toString()
    {
        String fmt = "AnalysisSite(node = {0}, instr = {1})";
        return format(fmt, node, instr);
    }

    public abstract int hashCode();

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Site == false) {
            return false;
        }

        // Note that the class definition asserts that all fields are non-null by construction.
        CallSite that = (CallSite) o;
        return node.equals(that.node)
            && instr.equals(that.instr);
    }
}
