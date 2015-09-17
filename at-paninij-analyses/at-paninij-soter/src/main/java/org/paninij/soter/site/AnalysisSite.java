package org.paninij.soter.site;

import javax.json.JsonObject;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;


public abstract class AnalysisSite
{
    protected final CGNode node;
    protected final SSAInstruction instruction;
    
    protected AnalysisSite(CGNode node, SSAInstruction instruction)
    {
        this.node = node;
        this.instruction = instruction;
    }

    public SSAInstruction getInstruction() {
        return instruction;
    }

    public CGNode getNode() {
        return node;
    }

    public abstract JsonObject toJson();
}
