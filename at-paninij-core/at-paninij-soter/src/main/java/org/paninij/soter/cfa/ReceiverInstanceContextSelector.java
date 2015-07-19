package org.paninij.soter.cfa;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Context;
import com.ibm.wala.ipa.callgraph.ContextSelector;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.ReceiverInstanceContext;
import com.ibm.wala.util.intset.EmptyIntSet;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.util.intset.IntSetUtil;


public class ReceiverInstanceContextSelector implements ContextSelector
{
    @Override
    public Context getCalleeTarget(CGNode caller, CallSiteReference site, IMethod callee,
                                   InstanceKey[] actualParameters)
    {
        // Context selection ignores all parameter instances except for the receiver parameter.
        if (actualParameters == null || callee.isStatic())
        {
            // If there is no receiver object, then the callee uses the same context as the caller.
            return caller.getContext();
        }
        else
        {
            // Use the receiver instance as the callee's context.
            return new ReceiverInstanceContext(actualParameters[0]);
        }
    }

    @Override
    public IntSet getRelevantParameters(CGNode caller, CallSiteReference site)
    {
        // Only the receiver is considered "relevant".
        if (site.isStatic()) {
            return new EmptyIntSet();
        } else {
            return IntSetUtil.make(new int[]{0});
        }
    }

}
