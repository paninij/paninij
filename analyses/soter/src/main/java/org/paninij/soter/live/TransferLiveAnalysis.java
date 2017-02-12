/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
package org.paninij.soter.live;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleCore;
import org.paninij.soter.transfer.TransferAnalysis;
import org.paninij.soter.transfer.TransferSite;
import org.paninij.soter.util.Analysis;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.ISSABasicBlock;

/**
 * When performed, the analysis analyzes the capsule core and the call graph analysis to
 * generate information about the variables which are alive at the capsule's "relevant" transfer
 * sites and call sites.
 * 
 * See Figure 8 of Negara, 2011.
 */
public class TransferLiveAnalysis extends Analysis
{
    // The analysis's dependencies:
    final protected CapsuleCore core;
    final protected LocalLiveAnalysisFactory llaFactory;
    final protected TransferAnalysis ta;
    final protected CallGraphAnalysis cga;
    final protected IClassHierarchy cha;
    
    // The results of the analysis:
    protected final Map<TransferSite, Set<PointerKey>> liveVariables;

    public TransferLiveAnalysis(CapsuleCore core,
                                LocalLiveAnalysisFactory llaFactory,
                                TransferAnalysis ta,
                                CallGraphAnalysis cga,
                                IClassHierarchy cha)
    {
        this.core = core;
        this.llaFactory = llaFactory;
        this.ta = ta;
        this.cga = cga;
        this.cha = cha;
        
        liveVariables = new HashMap<TransferSite, Set<PointerKey>>();
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
            Set<TransferSite> relevantSites = ta.getRelevantSites(node);
            LocalLiveAnalysis localAnalysis = llaFactory.lookupOrMake(node);
            localAnalysis.perform();
            for (TransferSite site : relevantSites) {
                addPointerKeysAfter(site);
            }
        }
    }


    protected void addPointerKeysAfter(TransferSite transferSite)
    {
        CGNode node = transferSite.getNode();
        assert cga.getCallGraph().containsNode(node);

        LocalLiveAnalysis localLiveAnalysis = llaFactory.lookupOrMake(node);
        localLiveAnalysis.perform();

        IR nodeIR = node.getIR();
        ISSABasicBlock block = nodeIR.getBasicBlockForInstruction(transferSite.getInstruction());
        liveVariables.put(transferSite, localLiveAnalysis.getPointerKeysAfter(block));
    }


    /**
     * @param transferSite A transfer site defined w.r.t. the factory's call graph analysis.
     * 
     * @return The set of pointer keys into the heap model of the factory's call graph analysis
     *         which the analysis found to be live after the program point of the given transfer
     *         site.
     */
    public Set<PointerKey> getLiveVariablesAfter(TransferSite transferSite)
    {
        assert hasBeenPerformed;
        return liveVariables.get(transferSite);
    }
}
