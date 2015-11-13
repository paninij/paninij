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
package org.paninij.soter.cga;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Context;
import com.ibm.wala.ipa.callgraph.ContextSelector;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.ReceiverInstanceContext;
import com.ibm.wala.util.intset.EmptyIntSet;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.util.intset.IntSetUtil;


public class ReceiverInstanceContextSelector implements ContextSelector
{
    @Override
    public Context getCalleeTarget(CGNode caller, CallSiteReference site, IMethod callee,
                                   InstanceKey[] actualParameters)
    {
        // Context selection ignores all parameter instances except for the receiver parameter.
        if (actualParameters == null || callee.isStatic())
        {
            // If there is no receiver object, then the callee uses the same context as the caller.
            return caller.getContext();
        }
        else
        {
            // Use the receiver instance as the callee's context.
            return new ReceiverInstanceContext(actualParameters[0]);
        }
    }

    @Override
    public IntSet getRelevantParameters(CGNode caller, CallSiteReference site)
    {
        // Only the receiver is considered "relevant".
        if (site.isStatic()) {
            return new EmptyIntSet();
        } else {
            return IntSetUtil.make(new int[]{0});
        }
    }

}
