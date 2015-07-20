package org.paninij.soter.live;

import org.paninij.soter.cga.CallGraphAnalysis;
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
    protected final CallGraphAnalysis cga;
    protected final TransferAnalysis ta;
    protected final TransferLiveAnalysis tla;
    protected final IClassHierarchy cha;

    protected boolean hasBeenPerformed;
    

    public CallGraphLiveAnalysis(CapsuleTemplate template, CallGraphAnalysis cga,
                                 TransferAnalysis ta, TransferLiveAnalysis tla,
                                 IClassHierarchy cha)
    {
        this.template = template;
        this.cga = cga;
        this.ta = ta;
        this.tla = tla;
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
