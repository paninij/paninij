package org.paninij.soter.site;

import com.ibm.wala.ssa.SSAReturnInstruction;


public interface IReturnSite extends ISite
{
    public SSAReturnInstruction getReturnInstruction(); 
}
