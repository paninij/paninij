package org.paninij.soter;

import org.paninij.soter.cfa.CallGraphAnalysis;
import org.paninij.soter.live.TransferSitesAnalysis;
import org.paninij.soter.live.TransferSitesLiveAnalysis;
import org.paninij.soter.model.CapsuleTemplate;

import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class TransferAnalysis implements Analysis
{
    // The analysis's dependencies:
    protected final CapsuleTemplate capsule;
    protected final CallGraphAnalysis cfa;
    protected final TransferSitesAnalysis transferSitesAnalysis;
    protected final TransferSitesLiveAnalysis transferSitesLiveAnalysis;
    protected final IClassHierarchy cha;
    
    protected boolean hasBeenPerformed;

    public TransferAnalysis(CapsuleTemplate capsule,
                            CallGraphAnalysis cfa,
                            TransferSitesAnalysis transferSitesAnalysis,
                            TransferSitesLiveAnalysis transferSitesLiveAnalysis,
                            IClassHierarchy cha)
    {
        this.capsule = capsule;
        this.cfa = cfa;
        this.transferSitesAnalysis = transferSitesAnalysis;
        this.transferSitesLiveAnalysis = transferSitesLiveAnalysis;
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
        transferSitesAnalysis.perform();
        transferSitesLiveAnalysis.perform();
        
        hasBeenPerformed = true;
    }

    public CallGraph getCallGraph()
    {
        return cfa.getCallGraph();
    }
 }
