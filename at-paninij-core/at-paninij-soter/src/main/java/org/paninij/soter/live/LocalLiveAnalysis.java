package org.paninij.soter.live;

import org.paninij.soter.cfa.CallGraphAnalysis;

import com.ibm.wala.dataflow.graph.BitVectorFramework;
import com.ibm.wala.dataflow.graph.BitVectorSolver;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.NullProgressMonitor;
import com.ibm.wala.util.MonitorUtil.IProgressMonitor;
import com.ibm.wala.util.graph.impl.GraphInverter;
import com.ibm.wala.util.intset.MutableMapping;
import com.ibm.wala.util.intset.OrdinalSetMapping;

import edu.illinois.soter.analysis.transferfunctionproviders.LocalLiveVariablesTransferFunctionProvider;

public class LocalLiveAnalysis
{
    CGNode node;
    CallGraphAnalysis cfa;
    
    OrdinalSetMapping<PointerKey> values;
    
    public LocalLiveAnalysis(CGNode node, CallGraphAnalysis cfa)
    {
        this.node = node;
        this.cfa = cfa;
        this.values = MutableMapping.make();
    }

    protected BitVectorSolver<ISSABasicBlock> perform()
    {
        SSACFG controlFlowGraph = node.getIR().getControlFlowGraph();
        LocalLiveVariablesTransferFunctionProvider transferFunctions
            = new LocalLiveVariablesTransferFunctionProvider(cfa.getHeapModel(), node, values);

        BitVectorFramework<ISSABasicBlock, PointerKey> bitVectorFramework
            = new BitVectorFramework<ISSABasicBlock, PointerKey>(GraphInverter.invert(controlFlowGraph), transferFunctions, values);

        BitVectorSolver<ISSABasicBlock> bitVectorSolver = new BitVectorSolver<ISSABasicBlock>(bitVectorFramework);
        try {
            bitVectorSolver.solve((IProgressMonitor) new NullProgressMonitor());
        } catch (CancelException ex) {
            ex.printStackTrace();
        }
        return bitVectorSolver;
    }
    
    public PointerKey getPointerKey(int value)
    {
        return values.getMappedObject(value);
    }
}