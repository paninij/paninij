package org.paninij.soter.site;

import static java.text.MessageFormat.format;

import javax.json.JsonObject;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;


public abstract class AnalysisSite
{
    protected final CGNode node;
    protected final SSAInstruction instr;
    
    protected AnalysisSite(CGNode node, SSAInstruction instr)
    {
        assert node != null;
        assert instr != null;
        this.node = node;
        this.instr = instr;
    }

    public SSAInstruction getInstruction() {
        return instr;
    }

    public CGNode getNode() {
        return node;
    }

    public abstract JsonObject toJson();
    
    @Override
    public String toString()
    {
        String fmt = "AnalysisSite(node = {0}, instr = {1})";
        return format(fmt, node, instr);
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Concrete subclasses must override `hashCode()`.");
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof AnalysisSite == false) {
            return false;
        }

        // Note that the class definition asserts that all fields are non-null by construction.
        AnalysisCallSite that = (AnalysisCallSite) o;
        return node.equals(that.node)
            && instr.equals(that.instr);
    }
}
