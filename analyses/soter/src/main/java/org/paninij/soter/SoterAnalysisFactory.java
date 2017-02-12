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
package org.paninij.soter;

import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.cga.CallGraphAnalysisFactory;
import org.paninij.soter.live.CallGraphLiveAnalysis;
import org.paninij.soter.live.CallGraphLiveAnalysisFactory;
import org.paninij.soter.live.TransferLiveAnalysis;
import org.paninij.soter.live.TransferLiveAnalysisFactory;
import org.paninij.soter.model.CapsuleCore;
import org.paninij.soter.model.CapsuleCoreFactory;
import org.paninij.soter.transfer.TransferAnalysis;
import org.paninij.soter.transfer.TransferAnalysisFactory;
import org.paninij.soter.util.WalaUtil;

import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.cha.IClassHierarchy;

/**
 * A factory for creating and performing `@PaniniJ` SOTER analyses. It caches resources that can be
 * used across multiple SOTER analyses (e.g. the class hierarchy analysis).
 */
public class SoterAnalysisFactory
{
    protected final IClassHierarchy cha;
    protected final AnalysisOptions options;
    protected final CapsuleCoreFactory coreFactory;
    protected final CallGraphAnalysisFactory cgaFactory;
    protected final TransferAnalysisFactory taFactory;
    protected final TransferLiveAnalysisFactory tlaFactory;
    protected final CallGraphLiveAnalysisFactory cglaFactory;


    public SoterAnalysisFactory(String classPath)
    {
        WalaUtil.checkRequiredResourcesExist();

        cha = WalaUtil.makeClassHierarchy(classPath);
        options = WalaUtil.makeAnalysisOptions(cha);
        
        coreFactory = new CapsuleCoreFactory(cha);
        cgaFactory = new CallGraphAnalysisFactory(cha, options);
        taFactory = new TransferAnalysisFactory(cha);
        tlaFactory = new TransferLiveAnalysisFactory(cha);
        cglaFactory = new CallGraphLiveAnalysisFactory(cha);
    }
    
    /**
     * @param capsuleName A fully qualified name of a capsule (e.g. "org.paninij.examples.pi.Pi").
     */
    public SoterAnalysis make(String capsuleName)
    {
        CapsuleCore core = coreFactory.make(capsuleName);
        CallGraphAnalysis cga = cgaFactory.make(core);
        TransferAnalysis ta = taFactory.make(core, cga);
        TransferLiveAnalysis tla = tlaFactory.make(core, cga, ta);
        CallGraphLiveAnalysis cgla = cglaFactory.make(core, cga, ta, tla);

        return new SoterAnalysis(core, cga, ta, tla, cgla, cha);
    }
}
