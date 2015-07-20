package org.paninij.soter.live;

import java.util.HashSet;
import java.util.Set;

import org.paninij.soter.cfa.CallGraphAnalysis;

import com.ibm.wala.dataflow.graph.BitVectorFramework;
import com.ibm.wala.dataflow.graph.BitVectorSolver;
import com.ibm.wala.dataflow.graph.ITransferFunctionProvider;
import com.ibm.wala.fixpoint.BitVectorVariable;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.NullProgressMonitor;
import com.ibm.wala.util.MonitorUtil.IProgressMonitor;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.impl.GraphInverter;
import com.ibm.wala.util.intset.IntIterator;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.util.intset.MutableMapping;
import com.ibm.wala.util.intset.OrdinalSetMapping;

import edu.illinois.soter.analysis.transferfunctionproviders.LocalLiveVariablesTransferFunctionProvider;

public class LocalLiveAnalysis
{
    protected final CGNode node;
    protected final CallGraphAnalysis cfa;
    
    protected final OrdinalSetMapping<PointerKey> latticeValues;
    protected final ITransferFunctionProvider<ISSABasicBlock, BitVectorVariable> transferFunctions;
    protected BitVectorFramework<ISSABasicBlock, PointerKey> dataFlowFramework;
    protected BitVectorSolver<ISSABasicBlock> dataFlowSolver;

    protected boolean hasBeenPerformed = false;
    
    public LocalLiveAnalysis(CGNode node, CallGraphAnalysis cfa)
    {
        this.node = node;
        this.cfa = cfa;

        latticeValues = MutableMapping.make();
        transferFunctions = new LocalLiveVariablesTransferFunctionProvider(cfa.getHeapModel(),
                                                                           node, latticeValues);
    }

    /**
     * Note that this is idempotent, that is, calling this after the first time has no effect.
     */
    protected void perform()
    {
        if (hasBeenPerformed) {
            return;
        }

        SSACFG cfg = node.getIR().getControlFlowGraph();
        Graph<ISSABasicBlock> invertedCFG = GraphInverter.invert(cfg);
        dataFlowFramework = new BitVectorFramework<ISSABasicBlock, PointerKey>(invertedCFG,
                                                                               transferFunctions,
                                                                               latticeValues);
        try {
            dataFlowSolver = new BitVectorSolver<ISSABasicBlock>(dataFlowFramework);
            dataFlowSolver.solve((IProgressMonitor) new NullProgressMonitor());
            hasBeenPerformed = true;
        } catch (CancelException ex) {
            String msg = "Caught unexpected `CancelException` while solving a `LocalLiveAnalysis`.";
            throw new RuntimeException(msg);
        }
    }
    
    
    /**
     * @param basicBlock A basic block in the CG node over which this analysis was performed.
     * 
     * @return The set of pointer keys representing the variables which the analysis found to be
     *         live after the given basic block.
     */
    protected Set<PointerKey> getPointerKeysAfter(ISSABasicBlock basicBlock)
    {
        // TODO: Add assertion which checks for the parameter's precondition.

        Set<PointerKey> pointerKeys = new HashSet<PointerKey>();

        // Note that we are calling `getIn()` because the graph was inverted.
        IntSet solutionValues = dataFlowSolver.getIn(basicBlock).getValue();
        if (solutionValues != null)
        {
            IntIterator iter = solutionValues.intIterator();
            while (iter.hasNext())
            {
                int variableIndex = iter.next();
                pointerKeys.add(latticeValues.getMappedObject(variableIndex));
            }
        }

        return pointerKeys;
    }
    
    public BitVectorSolver<ISSABasicBlock> getDataFlowSolver()
    {
        return dataFlowSolver;
    }
    
    public PointerKey getPointerKey(int value)
    {
        return latticeValues.getMappedObject(value);
    }
}