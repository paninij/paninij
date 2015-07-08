package org.paninij.soter2;

import java.util.function.Consumer;
import java.util.function.Function;

import com.ibm.wala.analysis.pointers.HeapGraph;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.HeapModel;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;

public abstract class ZeroOneCFA<I extends InstanceKey> 
{
    protected CallGraph callGraph;
    protected PointerAnalysis<I> pointerAnalysis;
    protected HeapModel heapModel;
    protected HeapGraph<I> heapGraph;
    
    
    /**
     * This performs a zero-one CFA algorithm which simultaneously builds the call graph and
     * performs the pointer analysis. It initializes all of the following fields:
     * 
     *  - callGraph
     *  - pointerAnalysis
     *  - heapModel
     *  - heapGraph
     */
    public abstract void perform();
    
    
    public void acceptUponCallGraph(Consumer<CallGraph> fn) {
        fn.accept(callGraph);
    }
    
    public void acceptUponPointerAnalysis(Consumer<PointerAnalysis<I>> fn) {
        fn.accept(pointerAnalysis);
    }
    
    public void acceptUponHeapModel(Consumer<HeapModel> fn) {
        fn.accept(heapModel);
    }
    
    public void acceptUponHeapGraph(Consumer<HeapGraph<I>> fn) {
        fn.accept(heapGraph);
    }
    
    public <R> R applyToCallGraph(Function<CallGraph, R> fn) {
        return fn.apply(callGraph);
    }

    public <R> R applyToPointerAnalysis(Function<PointerAnalysis<I>, R> fn) {
        return fn.apply(pointerAnalysis);
    }

    public <R> R applyToHeapModel(Function<HeapModel, R> fn) {
        return fn.apply(heapModel);
    }

    public <R> R applyToHeapGraph(Function<HeapGraph<I>, R> fn) {
        return fn.apply(heapGraph);
    }
}
