/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
package org.paninij.soter.live;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.util.Analysis;

import com.ibm.wala.dataflow.graph.AbstractMeetOperator;
import com.ibm.wala.dataflow.graph.BitVectorFramework;
import com.ibm.wala.dataflow.graph.BitVectorKillGen;
import com.ibm.wala.dataflow.graph.BitVectorSolver;
import com.ibm.wala.dataflow.graph.BitVectorUnion;
import com.ibm.wala.dataflow.graph.ITransferFunctionProvider;
import com.ibm.wala.fixpoint.BitVectorVariable;
import com.ibm.wala.fixpoint.UnaryOperator;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.NullProgressMonitor;
import com.ibm.wala.util.MonitorUtil.IProgressMonitor;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.impl.GraphInverter;
import com.ibm.wala.util.intset.BitVector;
import com.ibm.wala.util.intset.IntIterator;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.util.intset.MutableMapping;
import com.ibm.wala.util.intset.OrdinalSetMapping;


public class LocalLiveAnalysis extends Analysis
{
    // Analysis dependencies:
    protected final CGNode node;
    protected final CallGraphAnalysis cga;
    
    protected final OrdinalSetMapping<PointerKey> latticeValues;
    protected final ITransferFunctionProvider<ISSABasicBlock, BitVectorVariable> transferFunctions;
    protected BitVectorFramework<ISSABasicBlock, PointerKey> dataFlowFramework;
    protected BitVectorSolver<ISSABasicBlock> dataFlowSolver;

    public LocalLiveAnalysis(CGNode node, CallGraphAnalysis cga)
    {
        this.node = node;
        this.cga = cga;

        latticeValues = MutableMapping.make();
        transferFunctions = new TransferFunctionProvider();
    }


    @Override
    public void performAnalysis()
    {
        SSACFG cfg = node.getIR().getControlFlowGraph();
        Graph<ISSABasicBlock> invertedCFG = GraphInverter.invert(cfg);
        dataFlowFramework = new BitVectorFramework<ISSABasicBlock, PointerKey>(invertedCFG,
                                                                               transferFunctions,
                                                                               latticeValues);
        try {
            dataFlowSolver = new BitVectorSolver<ISSABasicBlock>(dataFlowFramework);
            dataFlowSolver.solve((IProgressMonitor) new NullProgressMonitor());
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
    public Set<PointerKey> getPointerKeysAfter(ISSABasicBlock basicBlock)
    {
        assert hasBeenPerformed;
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
        assert hasBeenPerformed;
        return dataFlowSolver;
    }


    public PointerKey getPointerKey(int value)
    {
        assert hasBeenPerformed;
        return latticeValues.getMappedObject(value);
    }
    
     
    private class TransferFunctionProvider implements ITransferFunctionProvider<ISSABasicBlock, BitVectorVariable>
    {
        @Override
        public AbstractMeetOperator<BitVectorVariable> getMeetOperator()
        {
            return BitVectorUnion.instance();
        }

        @Override
        public boolean hasEdgeTransferFunctions()
        {
            return false;
        }

        @Override
        public UnaryOperator<BitVectorVariable> getEdgeTransferFunction(ISSABasicBlock src,
                                                                        ISSABasicBlock dst)
        {
            String msg = "This transfer function provider doesn't provide node transfer functions.";
            throw new UnsupportedOperationException(msg);
        }

        @Override
        public boolean hasNodeTransferFunctions()
        {
            return true;
        }

        @Override
        public UnaryOperator<BitVectorVariable> getNodeTransferFunction(ISSABasicBlock node)
        {
            BitVector gen = new BitVector();
            BitVector kill = new BitVector();
            
            // Process instructions in reverse order.
            Stack<SSAInstruction> instructions = new Stack<SSAInstruction>();
            Iterator<SSAInstruction> instrIter = node.iterator();
            while (instrIter.hasNext()) {
                instructions.add(instrIter.next());
            }
            while (! instructions.isEmpty())
            {
                SSAInstruction instr = instructions.pop();

                for (int idx = 0; idx < instr.getNumberOfDefs(); idx++)
                {
                    int def = instr.getDef(idx);
                    int latticeValue = getLatticeValueFromLocalValueNumber(def);
                    kill.set(latticeValue);
                    gen.clear(latticeValue);
                }

                for (int idx = 0; idx < instr.getNumberOfUses(); idx++)
                {
                    int use = instr.getUse(idx);
                    int latticeValue = getLatticeValueFromLocalValueNumber(use);

                    if (use != -1)
                    {
                        gen.set(latticeValue);
                        kill.clear(latticeValue);
                    }
                }
            }
            
            return new BitVectorKillGen(kill, gen);
        }
        
        private int getLatticeValueFromLocalValueNumber(int valueNumber)
        {
            PointerKey ptr = cga.getHeapModel().getPointerKeyForLocal(node, valueNumber);
            return latticeValues.add(ptr);
        }
    }
}
