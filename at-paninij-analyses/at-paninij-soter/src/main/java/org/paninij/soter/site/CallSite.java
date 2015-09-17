package org.paninij.soter.site;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;

public interface CallSite
{
    public CallSiteReference getCallSite();

    public SSAAbstractInvokeInstruction getInvokeInstruction();
}
