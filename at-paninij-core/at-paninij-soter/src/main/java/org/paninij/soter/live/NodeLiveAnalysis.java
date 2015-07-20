package org.paninij.soter.live;

import org.paninij.soter.Analysis;

/**
 * See Figure 9 of Negara 2011.
 */
public class NodeLiveAnalysis implements Analysis
{
    protected boolean hasBeenPerformed;
    

    public NodeLiveAnalysis()
    {
        hasBeenPerformed = false;
        throw new UnsupportedOperationException("TODO");
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
