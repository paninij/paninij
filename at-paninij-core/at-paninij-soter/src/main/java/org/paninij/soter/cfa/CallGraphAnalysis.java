package org.paninij.soter.cfa;

import com.ibm.wala.analysis.pointers.HeapGraph;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;

public interface CallGraphAnalysis
{
    CallGraph getCallGraph();

    PointerAnalysis<InstanceKey> getPointerAnalysis();

    HeapGraph<InstanceKey> getHeapGraph();
}
