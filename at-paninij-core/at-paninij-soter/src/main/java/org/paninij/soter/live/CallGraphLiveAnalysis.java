package org.paninij.soter.live;

import java.util.HashMap;
import java.util.Map;

import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.transfer.TransferAnalysis;
import org.paninij.soter.transfer.TransferSite;
import org.paninij.soter.util.Analysis;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.util.intset.BitVector;
import com.ibm.wala.util.intset.MutableMapping;
import com.ibm.wala.util.intset.OrdinalSetMapping;

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
    
    Map<CGNode, Map<TransferSite, BitVector>> liveVariables;
    OrdinalSetMapping<PointerKey> globalLatticeValues;

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
        
        liveVariables = new HashMap<CGNode, Map<TransferSite, BitVector>>();
        globalLatticeValues = MutableMapping.make();
        
        hasBeenPerformed = false;
    }


    @Override
    public void perform()
    {
        if (hasBeenPerformed) {
            return;
        }
        
        prepareAnalysis();
        
        hasBeenPerformed = true;
        throw new UnsupportedOperationException("TODO");
    }


    /**
     * This collects the results of the sub-analyses and saves them into `liveVariables` and
     * `globalLatticeValues`.
     */
    protected void prepareAnalysis()
    {
        for (CGNode node : ta.getRelevantNodes())
        {
            Map<TransferSite, BitVector> nodeLiveVariables = new HashMap<TransferSite, BitVector>();
            for (TransferSite site : ta.getRelevantSites(node))
            {
                BitVector siteLiveVariables = new BitVector();

                // Always make the reciever object (i.e. `this`) be live:
                if (! node.getMethod().isStatic())
                {
                    PointerKey receiver = cga.getHeapModel().getPointerKeyForLocal(node, 1);
                    int key = globalLatticeValues.add(receiver);
                    siteLiveVariables.set(key);
                }

                for (PointerKey livePointerKey : tla.getPointerKeysAfter(site)) 
                {
                    int key = globalLatticeValues.add(livePointerKey);
                    siteLiveVariables.set(key);
                }
                
                nodeLiveVariables.put(site, siteLiveVariables);
            }
            
            liveVariables.put(node, nodeLiveVariables);
        }
    }
}
