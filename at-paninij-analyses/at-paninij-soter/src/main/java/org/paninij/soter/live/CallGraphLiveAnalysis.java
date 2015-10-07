package org.paninij.soter.live;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.site.AnalysisSite;
import org.paninij.soter.site.CallSite;
import org.paninij.soter.site.SiteAnalysis;
import org.paninij.soter.util.AnalysisJsonResultsCreator;
import org.paninij.soter.util.LoggingAnalysis;

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
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.cha.IClassHierarchy;
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
public class CallGraphLiveAnalysis extends LoggingAnalysis
{
    // Analysis dependencies.
    protected final CapsuleTemplate template;
    protected final CallGraphAnalysis cga;
    protected final SiteAnalysis sa;
    protected final TransferLiveAnalysis tla;
    protected final IClassHierarchy cha;
    
    protected final OrdinalSetMapping<PointerKey> globalLatticeValues;
    protected final Map<CGNode, Map<AnalysisSite, BitVector>> liveVariables;
    protected final ITransferFunctionProvider<CGNode, BitVectorVariable> transferFunctionProvider;
    BitVectorFramework<CGNode, PointerKey> dataFlowFramework;
    BitVectorSolver<CGNode> dataFlowSolver;

    protected final JsonResultsCreator jsonCreator;


    public CallGraphLiveAnalysis(CapsuleTemplate template, CallGraphAnalysis cga,
                                 SiteAnalysis sa, TransferLiveAnalysis tla,
                                 IClassHierarchy cha)
    {
        this.template = template;
        this.cga = cga;
        this.sa = sa;
        this.tla = tla;
        this.cha = cha;
        
        globalLatticeValues = MutableMapping.make();
        liveVariables = new HashMap<CGNode, Map<AnalysisSite, BitVector>>();
        transferFunctionProvider = new TransferFunctionProvider();
        
        jsonCreator = new JsonResultsCreator();
    }

    @Override
    public void performSubAnalyses()
    {
        cga.perform();
        sa.perform();
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
        for (CGNode node : sa.getReachingNodes())
        {
            Map<AnalysisSite, BitVector> nodeLiveVariables = new HashMap<AnalysisSite, BitVector>();
            for (AnalysisSite site : sa.getRelevantSites(node))
            {
                BitVector siteLiveVariables = new BitVector();

                // If a site is non-static, then make its `this` variable be live.
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
    // TODO: Move this process into `performAnalysis()`, rather than on-demand by clients.
    public Set<PointerKey> getLiveVariablesAfter(CGNode node)
    {
        assert hasBeenPerformed;

        Set<PointerKey> pointerKeys = new HashSet<PointerKey>();

        // Note that we are calling `getIn()` because `getOut()` does not work. (The transfer
        // function provider does not produce node transfer functions).
        // TODO: Try to enable `getOut()` to work.
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
            if (! sa.getReachingNodes().contains(src) || ! sa.getReachingNodes().contains(dest)) {
                return BitVectorIdentity.instance();
            }
            
            Map<AnalysisSite, BitVector> srcNodeLiveVariables = liveVariables.get(src);
            
            // Make an operator which unions with any argument the union of all live variables of
            // those call sites which call `dest` from `src`.
            BitVector union = new BitVector();
            for (CallSite site : sa.getRelevantCallers(dest))
            {
                if (src.equals(site.getNode())) {
                    BitVector transferSiteBitVector = srcNodeLiveVariables.get(site);
                    assert transferSiteBitVector != null;
                    union.or(transferSiteBitVector);
                }
            }
            return new BitVectorUnionVector(union);
        }

        @Override
        public boolean hasNodeTransferFunctions()
        {
            return false;
        }
  
        @Override
        public UnaryOperator<BitVectorVariable> getNodeTransferFunction(CGNode node)
        {
            String msg = "This provider does not provide any node transfer functions.";
            throw new UnsupportedOperationException(msg);
        }
    }

    @Override
    public JsonObject getJsonResults()
    {
        assert hasBeenPerformed;
        return jsonCreator.toJson();
    }

    @Override
    public String getJsonResultsString()
    {
        assert hasBeenPerformed;
        return jsonCreator.toJsonString();
    }

    @Override
    protected String getJsonResultsLogFileName()
    {
        return template.getQualifiedName().replace('/', '.') + ".json";
    }
    
    private class JsonResultsCreator extends AnalysisJsonResultsCreator
    {
        @Override
        public JsonObject toJson()
        {
            assert hasBeenPerformed;

            if (json != null) {
                return json;
            }

            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("globalLatticeValues", toJsonBuilder(globalLatticeValues));
            builder.add("liveVariables", liveVariablesJson());
            builder.add("dataFlowSolver", dataFlowSolverJson());
            builder.add("liveVariablesAfterReachingNodes", liveVariablesAfterReachingNodesJson());
            json = builder.build();
            return json;
        }
        
        private JsonArrayBuilder dataFlowSolverJson()
        {
            JsonArrayBuilder builder = Json.createArrayBuilder();
            for (CGNode node: sa.getReachingNodes()) {
                builder.add(dataFlowSolverNodeJson(node));
            }
            return builder;
        }
        
        private JsonObjectBuilder dataFlowSolverNodeJson(CGNode node)
        {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("node", toJsonBuilder(node));
            builder.add("in", dataFlowSolver.getIn(node).toString());
            //builder.add("out", dataFlowSolver.getOut(node).toString());
            return builder;
        }
        
        private JsonArrayBuilder liveVariablesAfterReachingNodesJson()
        {
            JsonArrayBuilder builder = Json.createArrayBuilder();
            for (CGNode node: sa.getReachingNodes()) {
                builder.add(liveVariablesAfterNodeJson(node));
            }
            return builder;
        }
        
        private JsonObjectBuilder liveVariablesAfterNodeJson(CGNode node)
        {
            JsonObjectBuilder objBuilder = Json.createObjectBuilder();
            objBuilder.add("node", toJsonBuilder(node));
            
            JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
            for (PointerKey liveVar: getLiveVariablesAfter(node)) {
                arrBuilder.add(toJson(liveVar));
            }
            objBuilder.add("liveVariables", arrBuilder);

            return objBuilder;
        }

        private JsonArrayBuilder liveVariablesJson()
        {
            JsonArrayBuilder builder = Json.createArrayBuilder();
            for (Entry<CGNode, Map<AnalysisSite, BitVector>> entry : liveVariables.entrySet()) {
                builder.add(liveVariablesEntryJson(entry.getKey(), entry.getValue()));
            }
            return builder;
        }
        
        private JsonObjectBuilder liveVariablesEntryJson(CGNode node, Map<AnalysisSite, BitVector> map)
        {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("node", toJsonBuilder(node));
            builder.add("analysisSites", liveVariablesSubMapJson(map));
            return builder;
        }
        
        private JsonArrayBuilder liveVariablesSubMapJson(Map<AnalysisSite, BitVector> map)
        {
            JsonArrayBuilder builder = Json.createArrayBuilder();
            for (Entry<AnalysisSite, BitVector> entry: map.entrySet())
            {
                builder.add(Json.createObjectBuilder()
                                .add("site", entry.getKey().toJson())
                                .add("bitVector", entry.getValue().toString()));
            }
            return builder;
        }
        
        @Override
        public CallGraph getCallGraph()
        {
            return cga.getCallGraph();
        }
    }
    
}
