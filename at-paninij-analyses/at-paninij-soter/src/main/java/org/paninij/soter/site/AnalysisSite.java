package org.paninij.soter.site;

import static java.text.MessageFormat.format;

import javax.json.JsonObject;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;


public abstract class AnalysisSite
{
    protected final CGNode node;
    protected final SSAInstruction instr;
    
    protected AnalysisSite(CGNode node, SSAInstruction instruction)
    {
        this.node = node;
        this.instr = instruction;
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
}
