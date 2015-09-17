package org.paninij.soter.site;

import javax.json.JsonObject;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;


public class AnalysisCallSite extends AnalysisSite implements CallSite
{
    protected final SSAAbstractInvokeInstruction invokeInstr;
    protected final CallSiteReference callSite;
    
    public static AnalysisCallSite make(CGNode node, CallSiteReference callSite)
    {
        // TODO: Figure out how this array of SSA instructions should be handled.
        SSAAbstractInvokeInstruction instrs[] = node.getIR().getCalls(callSite);
        if (instrs.length != 1)
        {
            String msg = "Expecting there to be exactly one SSA instruction "
                       + "associated with call site: " + callSite;
            throw new RuntimeException(msg);
        }
        return new AnalysisCallSite(node, instrs[0], callSite);
    }
    
    public AnalysisCallSite(CGNode node, SSAAbstractInvokeInstruction invokeInstr,
                            CallSiteReference callSite)
    {
        super(node, invokeInstr);
        assert callSite != null;

        this.invokeInstr = invokeInstr;
        this.callSite = callSite;
    }

    @Override
    public CallSiteReference getCallSite() {
        return callSite;
    }

    @Override
    public SSAAbstractInvokeInstruction getInvokeInstruction() {
        return invokeInstr;
    }

    @Override
    public JsonObject toJson()
    {
        throw new UnsupportedOperationException("TODO");
    }
}
