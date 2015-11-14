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

import org.paninij.soter.cga.CallGraphAnalysis;

import com.ibm.wala.ipa.callgraph.CGNode;

/**
 * Used to perform standard local live variable analyses on given call graph nodes. Note that this
 * factory memoizes all of its results until `clearCache()` is called.
 */
public class LocalLiveAnalysisFactory
{
    CallGraphAnalysis cga;
    Map<CGNode, LocalLiveAnalysis> cachedAnalyses;

    public LocalLiveAnalysisFactory(CallGraphAnalysis cga)
    {
        this.cga = cga;
        resetAnalysisCache();
    }
    
    /**
     * Creates a local live analysis instance over the given call graph node if this method has
     * never been called with this node before; otherwise, a previously created (i.e. cached)
     * analysis instance is returned.
     * 
     * Note that newly created analyses have not been performed (i.e. it is the responsibility of
     * the client to call `LocalLiveAnalysis.perform()`.
     */
    public LocalLiveAnalysis lookupOrMake(CGNode node)
    {
        LocalLiveAnalysis analysis = cachedAnalyses.get(node);
        if (analysis == null) {
            analysis = new LocalLiveAnalysis(node, cga);
            cachedAnalyses.put(node, analysis);
        }
        return analysis;
    }

    public void resetAnalysisCache()
    {
        cachedAnalyses = new HashMap<CGNode, LocalLiveAnalysis>();
    }
}
