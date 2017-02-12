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

import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleCore;
import org.paninij.soter.transfer.TransferAnalysis;

import com.ibm.wala.ipa.cha.IClassHierarchy;

public class TransferLiveAnalysisFactory
{
    protected final IClassHierarchy cha;

    public TransferLiveAnalysisFactory(IClassHierarchy cha)
    {
        this.cha = cha;
    }

    public TransferLiveAnalysis make(CapsuleCore core, CallGraphAnalysis cga,
                                     TransferAnalysis ta)
    {
        LocalLiveAnalysisFactory llaFactory = new LocalLiveAnalysisFactory(cga);
        return new TransferLiveAnalysis(core, llaFactory, ta, cga, cha);
    }
}
