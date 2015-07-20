package org.paninij.soter;

import org.paninij.soter.cfa.CallGraphAnalysis;
import org.paninij.soter.live.CallGraphLiveAnalysis;
import org.paninij.soter.live.TransferLiveAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.transfer.TransferAnalysis;
import org.paninij.soter.util.Analysis;

import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class SoterAnalysis implements Analysis
{
    // The analysis's dependencies:
    protected final CapsuleTemplate capsule;
    protected final CallGraphAnalysis cfa;
    protected final CallGraphLiveAnalysis nodeLiveAnalysis;
    protected final IClassHierarchy cha;
    
    protected boolean hasBeenPerformed;

    public SoterAnalysis(CapsuleTemplate capsule, CallGraphAnalysis cfa, CallGraphLiveAnalysis nodeLiveAnalysis,
                            IClassHierarchy cha)
    {
        this.capsule = capsule;
        this.cfa = cfa;
        this.nodeLiveAnalysis = nodeLiveAnalysis;
        this.cha = cha;
        
        hasBeenPerformed = false;
    }

    @Override
    public void perform()
    {
        if (hasBeenPerformed) {
            return;
        }

        cfa.perform();
        nodeLiveAnalysis.perform();
        
        hasBeenPerformed = true;
    }

    public CallGraph getCallGraph()
    {
        return cfa.getCallGraph();
    }
 }
