package org.paninij.soter.live;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.site.AnalysisSite;
import org.paninij.soter.transfer.TransferAnalysis;
import org.paninij.soter.util.AnalysisJsonResultsCreator;
import org.paninij.soter.util.LoggingAnalysis;
import org.paninij.soter.util.Sets;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.ISSABasicBlock;

/**
 * When performed, the analysis analyzes the capsule template and the call graph analysis to
 * generate information about the variables which are alive at the capsule's "relevant" transfer
 * sites and call sites.
 * 
 * See Figure 8 of Negara, 2011.
 */
public class TransferLiveAnalysis extends LoggingAnalysis
{
    // The analysis's dependencies:
    final protected CapsuleTemplate template;
    final protected LocalLiveAnalysisFactory llaFactory;
    final protected TransferAnalysis ta;
    final protected CallGraphAnalysis cga;
    final protected IClassHierarchy cha;
    
    // The results of the analysis:
    protected final Map<AnalysisSite, Set<PointerKey>> liveVariables;

    protected final JsonResultsCreator jsonCreator;


    public TransferLiveAnalysis(CapsuleTemplate template,
                                LocalLiveAnalysisFactory llaFactory,
                                TransferAnalysis ta,
                                CallGraphAnalysis cga,
                                IClassHierarchy cha)
    {
        this.template = template;
        this.llaFactory = llaFactory;
        this.ta = ta;
        this.cga = cga;
        this.cha = cha;
        
        liveVariables = new HashMap<AnalysisSite, Set<PointerKey>>();

        jsonCreator = new JsonResultsCreator();
    }

    @Override
    public void performSubAnalyses()
    {
        ta.perform();
    }
    

    @Override
    public void performAnalysis()
    {
        for (CGNode node : ta.getReachingNodes())
        {
            Set<AnalysisSite> relevantSites = ta.getRelevantSites(node);
            LocalLiveAnalysis localAnalysis = llaFactory.lookupOrMake(node);
            localAnalysis.perform();
            for (AnalysisSite site : relevantSites) {
                addLiveVariablesAfter(site);
            }
        }
    }


    protected void addLiveVariablesAfter(AnalysisSite site)
    {
        CGNode node = site.getNode();
        assert cga.getCallGraph().containsNode(node);

        LocalLiveAnalysis localLiveAnalysis = llaFactory.lookupOrMake(node);
        localLiveAnalysis.perform();

        IR nodeIR = node.getIR();
        ISSABasicBlock block = nodeIR.getBasicBlockForInstruction(site.getInstruction());
        liveVariables.put(site, localLiveAnalysis.getPointerKeysAfter(block));
    }


    /**
     * @param site A transfer site defined w.r.t. the factory's call graph analysis.
     * 
     * @return The set of pointer keys into the heap model of the factory's call graph analysis
     *         which the analysis found to be live after the program point of the given transfer
     *         site.
     */
    public Set<PointerKey> getLiveVariablesAfter(AnalysisSite site)
    {
        assert hasBeenPerformed;
        return liveVariables.get(site);
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
            builder.add("liveVariables", ptrMapToJsonBuilder(liveVariables));
            
            json = builder.build();
            return json;
        }

        @Override
        public CallGraph getCallGraph()
        {
            return cga.getCallGraph();
        }
    }
    
    
    @Override
    public boolean checkPostConditions()
    {
        // Check that the domain of `liveVariables` is equivalent to the union of all analysis sites
        // defined within the set of all reaching nodes.
        Set<AnalysisSite> domain = new HashSet<AnalysisSite>();
        for (CGNode node: ta.getReachingNodes()) {
            domain.addAll(ta.getRelevantSites(node));
        }
        if (!Sets.isWellDefinedOverDomain(liveVariables, domain)) {
            return false;
        }
        return true;
    }
}
