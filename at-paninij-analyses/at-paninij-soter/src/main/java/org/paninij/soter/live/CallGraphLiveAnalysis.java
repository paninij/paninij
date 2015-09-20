package org.paninij.soter.live;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.site.AnalysisCallSite;
import org.paninij.soter.site.AnalysisSite;
import org.paninij.soter.site.CallSite;
import org.paninij.soter.site.TransferringSite;
import org.paninij.soter.transfer.TransferAnalysis;
import org.paninij.soter.util.Analysis;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.dataflow.graph.AbstractMeetOperator;
import com.ibm.wala.dataflow.graph.BitVectorFramework;
import com.ibm.wala.dataflow.graph.BitVectorIdentity;
import com.ibm.wala.dataflow.graph.BitVectorSolver;
import com.ibm.wala.dataflow.graph.BitVectorUnion;
import com.ibm.wala.dataflow.graph.BitVectorUnionVector;
import com.ibm.wala.dataflow.graph.ITransferFunctionProvider;
import com.ibm.wala.fixpoint.BitVectorVariable;
import com.ibm.wala.fixpoint.UnaryOperator;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.NullProgressMonitor;
import com.ibm.wala.util.intset.BitVector;
import com.ibm.wala.util.intset.IntIterator;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.util.intset.MutableMapping;
import com.ibm.wala.util.intset.OrdinalSetMapping;


/**
 * See Figure 9 of Negara 2011.
 */
public class CallGraphLiveAnalysis extends Analysis
{
    // Analysis dependencies.
    protected final CapsuleTemplate template;
    protected final CallGraphAnalysis cga;
    protected final TransferAnalysis ta;
    protected final TransferLiveAnalysis tla;
    protected final IClassHierarchy cha;
    
    ITransferFunctionProvider<CGNode, BitVectorVariable> transferFunctionProvider;
    Map<CGNode, Map<AnalysisSite, BitVector>> liveVariables;
    OrdinalSetMapping<PointerKey> globalLatticeValues;
    BitVectorFramework<CGNode, PointerKey> dataFlowFramework;
    BitVectorSolver<CGNode> dataFlowSolver;


    public CallGraphLiveAnalysis(CapsuleTemplate template, CallGraphAnalysis cga,
                                 TransferAnalysis ta, TransferLiveAnalysis tla,
                                 IClassHierarchy cha)
    {
        this.template = template;
        this.cga = cga;
        this.ta = ta;
        this.tla = tla;
        this.cha = cha;
        
        transferFunctionProvider = new TransferFunctionProvider();
        liveVariables = new HashMap<CGNode, Map<AnalysisSite, BitVector>>();
        globalLatticeValues = MutableMapping.make();
        
        hasBeenPerformed = false;
    }

    @Override
    public void performSubAnalyses()
    {
        cga.perform();
        ta.perform();
        tla.perform();
    }

    @Override
    public void performAnalysis()
    {
        collectSubanalysisResults();

        try {
            dataFlowFramework = new BitVectorFramework<CGNode, PointerKey>(cga.getCallGraph(),
                                                                           transferFunctionProvider,
                                                                           globalLatticeValues);
            dataFlowSolver = new BitVectorSolver<CGNode>(dataFlowFramework);
            dataFlowSolver.solve(new NullProgressMonitor());
        }
        catch (CancelException ex) {
            String msg = "Caught unexpected `CancelException` while solving a `CallGraphLiveAnalysis`.";
            throw new RuntimeException(msg); 
        }
    }


    /**
     * This collects the results of the sub-analyses and saves them into `liveVariables` and
     * `globalLatticeValues`.
     */
    protected void collectSubanalysisResults()
    {
        for (CGNode node : ta.getReachingNodes())
        {
            Map<AnalysisSite, BitVector> nodeLiveVariables = new HashMap<AnalysisSite, BitVector>();
            for (AnalysisSite site : ta.getRelevantSites(node))
            {
                BitVector siteLiveVariables = new BitVector();

                // Always make the reciever object (i.e. `this`) be live:
                if (! node.getMethod().isStatic())
                {
                    PointerKey receiver = cga.getHeapModel().getPointerKeyForLocal(node, 1);
                    int key = globalLatticeValues.add(receiver);
                    siteLiveVariables.set(key);
                }

                for (PointerKey livePointerKey : tla.getLiveVariablesAfter(site)) 
                {
                    int key = globalLatticeValues.add(livePointerKey);
                    siteLiveVariables.set(key);
                }
                
                nodeLiveVariables.put(site, siteLiveVariables);
            }
            
            liveVariables.put(node, nodeLiveVariables);
        }
    }
    
    /**
     * @return The set of pointer keys representing the variables which the analysis found to be
     *         live after the given call graph node.
     */
    public Set<PointerKey> getLiveVariablesAfter(CGNode node)
    {
        assert hasBeenPerformed;

        Set<PointerKey> pointerKeys = new HashSet<PointerKey>();

        // Note that we are calling `getIn()` because `getOut()` does not work. (The transfer
        // function provider does not produce node transfer functions).
        IntSet solutionValues = dataFlowSolver.getIn(node).getValue();
        if (solutionValues != null)
        {
            IntIterator iter = solutionValues.intIterator();
            while (iter.hasNext())
            {
                int variableIndex = iter.next();
                pointerKeys.add(globalLatticeValues.getMappedObject(variableIndex));
            }
        }

        return pointerKeys;
    }
    
    
    private class TransferFunctionProvider implements ITransferFunctionProvider<CGNode, BitVectorVariable>
    {
        @Override
        public AbstractMeetOperator<BitVectorVariable> getMeetOperator()
        {
            return BitVectorUnion.instance();
        }

        @Override
        public boolean hasEdgeTransferFunctions()
        {
            return true;
        }
 
        @Override
        public UnaryOperator<BitVectorVariable> getEdgeTransferFunction(CGNode src, CGNode dest)
        {
            if (! ta.getReachingNodes().contains(src) || ! ta.getReachingNodes().contains(dest)) {
                return BitVectorIdentity.instance();
            }
            
            Map<AnalysisSite, BitVector> srcNodeLiveVariables = liveVariables.get(src);
            
            BitVector union = new BitVector();
            for (CallSite callSite : getCallSitesBetween(src, dest))
            {
                BitVector transferSiteBitVector = srcNodeLiveVariables.get(callSite);
                assert transferSiteBitVector != null;
                union.or(transferSiteBitVector);
            }
            return new BitVectorUnionVector(union);
        }
        
        private Set<CallSite> getCallSitesBetween(CGNode src, CGNode dest)
        {
            // TODO: Simplify this by using `CallSiteReference`s instead of SSA instructions?
            Set<CallSite> callSitesBetween = new HashSet<CallSite>();
            Set<SSAInstruction> instructionsBetween = getInstructionsBetween(src, dest);

            // Find that subset of this node's relevant transfer sites which are invocations from
            // the `src` node to the `dest` node.
            for (AnalysisSite analysisSite : ta.getRelevantSites(src))
            {
                // No `RETURN` kind transfer site will be an invocation from `src` to `dest`.
                if (analysisSite instanceof CallSite) {
                    for (SSAInstruction instr : instructionsBetween)
                    {
                        if (analysisSite.getInstruction().equals(instr)) {
                            callSitesBetween.add((CallSite) analysisSite);
                            break;
                        }
                    }
                }
            }

            return callSitesBetween;
        }
        
        private Set<SSAInstruction> getInstructionsBetween(CGNode src, CGNode dest)
        {
            Set<SSAInstruction> between = new HashSet<SSAInstruction>();

            Iterator<CallSiteReference> callSiteIter = cga.getCallGraph().getPossibleSites(src, dest);
            IR nodeIR = src.getIR();
            while (callSiteIter.hasNext())
            {
                CallSiteReference callSite = callSiteIter.next();
                SSAAbstractInvokeInstruction[] callSiteInstructions = nodeIR.getCalls(callSite);
                assert callSiteInstructions.length == 1;
                between.add(callSiteInstructions[0]);
            } 
            
            return between;
        }

        @Override
        public boolean hasNodeTransferFunctions()
        {
            return false;
        }
  
        @Override
        public UnaryOperator<BitVectorVariable> getNodeTransferFunction(CGNode node)
        {
            String msg = "This provider does not provide any edge transfer functions.";
            throw new UnsupportedOperationException(msg);
        }
    }
}
