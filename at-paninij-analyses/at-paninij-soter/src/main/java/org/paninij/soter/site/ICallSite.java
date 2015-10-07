package org.paninij.soter.site;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;

public interface ICallSite extends ISite
{
    public CallSiteReference getCallSite();

    public SSAAbstractInvokeInstruction getInvokeInstruction();
}
