package org.paninij.soter.site;

import static java.text.MessageFormat.format;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;


public class CallSite extends Site implements ICallSite
{
    protected final SSAAbstractInvokeInstruction invokeInstr;
    protected final CallSiteReference callSite;
    
    public static CallSite make(CGNode node, CallSiteReference callSite)
    {
        // TODO: Figure out how this array of SSA instructions should be handled.
        SSAAbstractInvokeInstruction instrs[] = node.getIR().getCalls(callSite);
        if (instrs.length != 1)
        {
            String msg = "Expecting exactly one SSA instruction for this call site:" + callSite;
            throw new RuntimeException(msg);
        }
        return new CallSite(node, instrs[0], callSite);
    }
    
    public CallSite(CGNode node, SSAAbstractInvokeInstruction invokeInstr,
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
    public JsonObjectBuilder toJsonBuilder()
    {
        return Json.createObjectBuilder()
                .add("type", "CallSite")
                .add("sourceMethod", node.getMethod().getSignature())
                .add("programCounter", invokeInstr.getProgramCounter())
                .add("targetMethod", invokeInstr.getDeclaredTarget().getSignature())
                .add("invokeInstr", invokeInstr.toString())
                .add("callSite", callSite.toString())
                .add("iindex", invokeInstr.iindex)
                .add("method", node.getMethod().getSignature())
                .add("context", node.getContext().toString())
                .add("hashCode", hashCode());
    }
    
    @Override
    public String toString()
    {
        String fmt = "CallSite(node = {0}, invokeInstr = {1}, callSite = {2})";
        return format(fmt, node, invokeInstr, callSite);
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        // Note that the class definition asserts that all fields are non-null by construction.
        result = prime * result + node.hashCode();
        result = prime * result + instr.hashCode();
        result = prime * result + invokeInstr.hashCode();
        result = prime * result + callSite.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof CallSite == false) {
            return false;
        }

        // Note that the class definition asserts that all fields are non-null by construction.
        CallSite that = (CallSite) o;
        return super.equals(that)
            && invokeInstr.equals(that.invokeInstr)
            && callSite.equals(that.callSite);
    }
}
