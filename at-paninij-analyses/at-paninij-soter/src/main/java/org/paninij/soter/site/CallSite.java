package org.paninij.soter.site;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;

public interface CallSite
{
    public CGNode getNode();
    
    public CallSiteReference getCallSite();

    public SSAAbstractInvokeInstruction getInvokeInstruction();
}
