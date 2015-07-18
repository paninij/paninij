package org.paninij.soter.model;

import com.ibm.wala.analysis.pointers.HeapGraph;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class Analysis
{
    protected Capsule capsule;
    protected CallGraph callGraph;
    protected HeapGraph<InstanceKey> heapGraph;
    protected PointerAnalysis<InstanceKey> pointerAnalysis;
    protected IClassHierarchy cha;

    public Analysis(Capsule capsule, CallGraph callGraph, HeapGraph<InstanceKey> heapGraph,
                    PointerAnalysis<InstanceKey> pointerAnalysis, IClassHierarchy cha)
    {
        this.capsule = capsule;
        this.callGraph = callGraph;
        this.heapGraph = heapGraph;
        this.pointerAnalysis = pointerAnalysis;
        this.cha = cha;
    }

    /**
     * @return The zero-one CFA call graph starting from the capsule being analyzed.
     */
    public CallGraph getCallGraph()
    {
        return callGraph;
    }
    
    /**
     * @return The capsule being analyzed.
     */
    public Capsule getCapsule()
    {
        return capsule;
    }
}
