package org.paninij.soter.live;

import org.paninij.soter.cfa.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.transfer.TransferAnalysis;
import org.paninij.soter.util.Analysis;

import com.ibm.wala.ipa.cha.IClassHierarchy;

/**
 * See Figure 9 of Negara 2011.
 */
public class CallGraphLiveAnalysis implements Analysis
{
    // Analysis dependencies.
    protected final CapsuleTemplate template;
    protected final CallGraphAnalysis cfa;
    protected final TransferAnalysis tsa;
    protected final TransferLiveAnalysis tsla;
    protected final IClassHierarchy cha;

    protected boolean hasBeenPerformed;
    

    public CallGraphLiveAnalysis(CapsuleTemplate template, CallGraphAnalysis cfa,
                                 TransferAnalysis tsa, TransferLiveAnalysis tsla,
                                 IClassHierarchy cha)
    {
        this.template = template;
        this.cfa = cfa;
        this.tsa = tsa;
        this.tsla = tsla;
        this.cha = cha;
        
        hasBeenPerformed = false;
    }


    @Override
    public void perform()
    {
        if (hasBeenPerformed) {
            return;
        }
        
        hasBeenPerformed = true;
        throw new UnsupportedOperationException("TODO");
    }
}
