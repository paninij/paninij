package org.paninij.soter;

import static org.paninij.soter.util.SoterUtil.makePointsToClosure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.paninij.runtime.util.IdentitySet;
import org.paninij.runtime.util.IntMap;
import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.live.CallGraphLiveAnalysis;
import org.paninij.soter.live.TransferLiveAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.transfer.TransferAnalysis;
import org.paninij.soter.transfer.TransferSite;
import org.paninij.soter.util.Analysis;

import com.ibm.wala.analysis.pointers.HeapGraph;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.HeapModel;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.util.intset.IntIterator;

public class SoterAnalysis implements Analysis
{
    // The analysis's dependencies:
    protected final CapsuleTemplate template;
    protected final CallGraphAnalysis cga;
    protected final TransferAnalysis ta;
    protected final TransferLiveAnalysis tla;
    protected final CallGraphLiveAnalysis cgla;
    protected final IClassHierarchy cha;
    
    // The analysis's results:
    protected final Map<TransferSite, TransferSiteResults> transferSiteResultsMap;

    // TODO: Make a single analysis results object for each `TransferSite`, that way there only
    // needs to be one map.
    
    protected boolean hasBeenPerformed;


    public SoterAnalysis(CapsuleTemplate template, CallGraphAnalysis cga, TransferAnalysis ta,
                         TransferLiveAnalysis tla, CallGraphLiveAnalysis cgla, IClassHierarchy cha)
    {
        this.template = template;
        this.cga = cga;
        this.ta = ta;
        this.tla = tla;
        this.cgla = cgla;
        this.cha = cha;
        
        transferSiteResultsMap = new HashMap<TransferSite, TransferSiteResults>();
        
        hasBeenPerformed = false;
    }


    @Override
    public void perform()
    {
        if (hasBeenPerformed) {
            return;
        }

        cga.perform();
        ta.perform();
        tla.perform();
        cgla.perform();
        
        for (CGNode transferringNode : ta.getTransferringNodes())
        {
            for (TransferSite transferSite : ta.getTransferringSites(transferringNode))
            {
                TransferSiteResults results = new TransferSiteResults();
                
                // Find all of the live variables for this transfer site.
                results.liveVariables = new HashSet<PointerKey>();
                results.liveVariables.addAll(tla.getPointerKeysAfter(transferSite));
                results.liveVariables.addAll(cgla.getPointerKeysAfter(transferringNode));
                
                // Find all of the (transitively) live objects.
                results.liveObjects = new IdentitySet<Object>();
                for (PointerKey pointerKey : results.liveVariables) {
                    results.liveObjects.addAll(makePointsToClosure(pointerKey, cga.getHeapGraph()));
                }
                
                // For each of the transfer site's transfers, find all of the (transitively)
                // escaped objects.
                HeapModel heapModel = cga.getHeapModel();
                HeapGraph<InstanceKey> heapGraph = cga.getHeapGraph();
                IntIterator paramIter = transferSite.getTransfers().intIterator();
                while (paramIter.hasNext())
                {
                    int paramID = paramIter.next();

                    PointerKey ptr = heapModel.getPointerKeyForLocal(transferringNode, paramID);
                    IdentitySet<Object> escaped = makePointsToClosure(ptr, heapGraph);
                    results.setEscapedObjects(paramID, escaped);

                    boolean isSafeTransfer = results.liveObjects.isDisjointFrom(escaped);
                    results.setTransferSafety(paramID, isSafeTransfer);
                }
            }
        }
        
        hasBeenPerformed = true;
    }


    public CallGraph getCallGraph()
    {
        return cga.getCallGraph();
    }
    
    
    /**
     * A simple container class to hold all of the results which the analysis generates for a single
     * transfer site.
     */
    private final static class TransferSiteResults
    {
        IntMap<ParameterResults> parameterResultsMap = new IntMap<ParameterResults>();
        Set<PointerKey> liveVariables;
        private IdentitySet<Object> liveObjects;
        
        public void setEscapedObjects(int paramID, IdentitySet<Object> escapedObjects)
        {
            ParameterResults paramResults = parameterResultsMap.get(paramID);
            if (paramResults == null)
            {
                paramResults = new ParameterResults();
                parameterResultsMap.put(paramID, paramResults);
            }
            paramResults.escapedObjects = escapedObjects;
        }
        
        public IdentitySet<Object> getEscapedObjects(int paramID)
        {
            return parameterResultsMap.get(paramID).escapedObjects;
        }
     
        public void setTransferSafety(int paramID, boolean isSafeTransfer)
        {
            ParameterResults paramResults = parameterResultsMap.get(paramID);
            if (paramResults == null)
            {
                paramResults = new ParameterResults();
                parameterResultsMap.put(paramID, paramResults);
            }
            paramResults.isSafeTransfer = isSafeTransfer;
        }
        
        public boolean getTransferSafety(int paramID)
        {
            return parameterResultsMap.get(paramID).isSafeTransfer;
        }
    }
    
    
    /**
     * A simple container class to hold all of the results which the analysis generates for a
     * single transfer of a single transfer site (i.e. a single parameter of a transfer site).
     */
    private final static class ParameterResults
    {
        IdentitySet<Object> escapedObjects;
        boolean isSafeTransfer;
    }
 }
