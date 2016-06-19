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
package org.paninij.soter.util;

import static org.paninij.soter.util.PaniniModel.isCapsuleInterface;

import java.util.Iterator;
import java.util.Set;
import java.util.function.BiPredicate;

import org.paninij.runtime.util.IdentitySet;
import org.paninij.runtime.util.IdentityStack;

import com.ibm.wala.analysis.pointers.HeapGraph;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeReference;

public class SoterUtil
{
    /**
     * Makes and returns the called-by closure of the given set of initial nodes w.r.t. the given
     * call graph, `cg`. For two nodes in `cg`, `m` and `n`, the called-by relation (for this `cg`)
     * 
     *     m CB n
     *      
     * is satisfied iff `m` is called by `n` (i.e. `n` is a successor of `m`).
     * 
     * This means that a node `n` in `cg` will be in the returned set iff `cg` includes
     * a finite sequence of (forward) call edges from `n` to some node in `init`.
     * 
     * Note: The returned set does potentially include the fake root node.
     */
    public static IdentitySet<CGNode> makeCalledByClosure(IdentitySet<CGNode> init, CallGraph cg)
    {
        IdentitySet<CGNode> closure = new IdentitySet<CGNode>();
        IdentityStack<CGNode> workstack = new IdentityStack<CGNode>();

        for (CGNode m : init)
        {
            workstack.add(m);
            closure.add(m);
        }

        // In each iteration, for some `m`, find the all nodes `n` satisfying `m CB n`:
        CGNode m, n;
        while ((m = workstack.pop()) != null)
        {
            Iterator<CGNode> preds = cg.getPredNodes(m);
            while (preds.hasNext())
            {
                // If `n` should not be ignored and has not yet been added to `closure`, add it to
                // both `closure` and `workstack`.
                n = preds.next();
                if (closure.add(n)) {
                    workstack.push(n);
                }
            }
        }
        return closure;
    }
    
    public static IdentitySet<CGNode> makeCalledByClosure(Set<CGNode> init, CallGraph cg)
    {
        return makeCalledByClosure(IdentitySet.make(init), cg);
    }


    /**
     * Makes and returns the points-to closure of the given `PointerKey` w.r.t. the given heap
     * graph, `hg`. For two nodes in `hg`, `p` and `q`, the points-to relation (for this `hg`)
     * 
     *     p PT q
     *      
     * is satisfied iff `p` points to `q`.
     * 
     * TODO: This is not a good description, because it does not take into account the alternation
     * between `PointerKey` nodes and `InstanceKey` nodes.
     */
    public static IdentitySet<InstanceKey> makePointsToClosure(PointerKey ptr,
                                                               HeapGraph<InstanceKey> hg)
    {
        IdentitySet<InstanceKey> closure = new IdentitySet<InstanceKey>();

        IdentityStack<PointerKey> workstack = new IdentityStack<PointerKey>();
        InstanceKey instanceKey;
        Iterator<Object> instanceKeyIter;

        workstack.push(ptr);
        while ((ptr = workstack.pop()) != null)
        {
            // Explore the pointer key.
            instanceKeyIter = hg.getSuccNodes(ptr);
            while (instanceKeyIter.hasNext())
            {
                // Explore all of its instance keys.
                instanceKey = (InstanceKey) instanceKeyIter.next();
                if (!closure.contains(instanceKey) && !isKnownToBeEffectivelyImmutable(instanceKey))
                {
                    closure.add(instanceKey);
                    Iterator<Object> pointerKeyIter = hg.getSuccNodes(instanceKey);
                    while (pointerKeyIter.hasNext()) {
                        workstack.push((PointerKey) pointerKeyIter.next());
                    }
                }
            }
        }
        return closure;
    }
    
    
    public static boolean isKnownToBeEffectivelyImmutable(InstanceKey instanceKey)
    {
        return isKnownToBeEffectivelyImmutable(instanceKey.getConcreteType().getName().toString());
    }
    
    
    public static boolean isKnownToBeEffectivelyImmutable(String className)
    {
        return className.equals("Ljava/lang/Character")
            || className.equals("Ljava/lang/Byte")
            || className.equals("Ljava/lang/Short")
            || className.equals("Ljava/lang/Integer")
            || className.equals("Ljava/lang/Long")
            || className.equals("Ljava/lang/Float")
            || className.equals("Ljava/lang/Double")
            || className.equals("Ljava/lang/Boolean")
            || className.equals("Ljava/lang/String");

    }
    
    
    public static boolean isKnownToBeEffectivelyImmutable(TypeReference typeRef)
    {
        return isKnownToBeEffectivelyImmutable(typeRef.getName().toString());
    }
    
    
    /**
     * Inspects the given call graph and returns a new `IdentitySet` holding references to those
     * call graph nodes which include at least one call site satisfying the given predicate.
     */
    public static IdentitySet<CGNode> getNodesFilteredByCallSite(CallGraph cg, BiPredicate<CGNode, CallSiteReference> p)
    {
        IdentitySet<CGNode> invokers = new IdentitySet<CGNode>();

        for (CGNode n : cg)
        {
            Iterator<CallSiteReference> callSites = n.iterateCallSites();
            while (callSites.hasNext())
            {
                if (p.test(n, callSites.next())) {
                    invokers.add(n);
                }
            }
        }

        return invokers;
    }

    
    /**
     * @return True iff the given call site indicates that the enclosing call graph node is
     * considered an "initial" node.
     */
    private static boolean isInitialNodeByCallSite(CGNode node, CallSiteReference callSite)
    {
        IClassHierarchy cha = node.getClassHierarchy();
        MethodReference calleeMethod = callSite.getDeclaredTarget();
        IClass calleeClass = cha.lookupClass(calleeMethod.getDeclaringClass());

        if (calleeClass == null || isCapsuleInterface(calleeClass) == false) {
            return false;
        }
        if (calleeMethod.getNumberOfParameters() == 0) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Inspects the given call graph and returns a new `IdentitySet` holding references to "initial"
     * SOTER call nodes. A node is "initial" if it contains a call site which is a remote procedure
     * invocation which passes arguments.
     */
    public static IdentitySet<CGNode> getInitialCallNodes(CallGraph cg)
    {
        return getNodesFilteredByCallSite(cg, ((n, cs) -> isInitialNodeByCallSite(n, cs)));
    }
}
