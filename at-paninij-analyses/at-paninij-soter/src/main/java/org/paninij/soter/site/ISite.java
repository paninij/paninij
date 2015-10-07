package org.paninij.soter.site;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

public interface ISite
{
    CGNode getNode();
    
    SSAInstruction getInstruction();
    
    JsonObject toJson();
    
    JsonObjectBuilder toJsonBuilder();
}
