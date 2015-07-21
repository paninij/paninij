package org.paninij.soter;

import org.paninij.soter.cga.CallGraphAnalysis;
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
    protected final CapsuleTemplate template;
    protected final CallGraphAnalysis cga;
    protected final TransferAnalysis ta;
    protected final TransferLiveAnalysis tla;
    protected final CallGraphLiveAnalysis cgla;
    protected final IClassHierarchy cha;
    
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
        
        hasBeenPerformed = true;
    }

    public CallGraph getCallGraph()
    {
        return cga.getCallGraph();
    }
 }
