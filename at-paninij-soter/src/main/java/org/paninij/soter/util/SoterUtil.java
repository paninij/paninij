package org.paninij.soter.util;

import static org.paninij.soter.util.PaniniModel.isCapsuleInterface;

import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.paninij.runtime.util.IdentitySet;
import org.paninij.runtime.util.IdentityStack;

import com.ibm.wala.analysis.pointers.HeapGraph;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
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
     *      m CB n
     *      
     *  is satisfied iff `m` is called by `n` (i.e. `n` is a successor of `m`).
     * 
     * This means that a node `n` in `cg` will be in the returned set iff `cg` includes
     * a finite sequence of (forward) call edges from `n` to some node in `init`.
     */
    public static IdentitySet<CGNode> makeCalledByClosure(IdentitySet<CGNode> init, CallGraph cg)
    {
        IClass fakeRootClass = cg.getFakeRootNode().getMethod().getDeclaringClass();
        Predicate<CGNode> ignore = (n -> n.getMethod()
                                          .getDeclaringClass()
                                          .equals(fakeRootClass));
        
        IdentitySet<CGNode> closure = new IdentitySet<CGNode>();
        IdentityStack<CGNode> workstack = new IdentityStack<CGNode>();

        for (CGNode m : init)
        {
            workstack.add(m);
            closure.add(m);
        }

        // Find the all nodes `n` satisfying the relation `m CB n`:
        CGNode m, n;
        while ((m = workstack.pop()) != null)
        {
            Iterator<CGNode> preds = cg.getPredNodes(m);
            while (preds.hasNext())
            {
                // If `n` should not be ignored and has not yet been added to `closure`, add it to
                // both `closure` and `workstack`.
                n = preds.next();
                if (ignore.test(n) == false && closure.add(n)) {
                    workstack.push(n);
                }
            }
        }
        return closure;
    }


    /**
     * Makes and returns the points-to closure of the given `PointerKey` w.r.t. the given heap
     * graph, `hg`. For two nodes in `hg`, `p` and `q`, the points-to relation (for this `hg`)
     * 
     *      p PT q
     *      
     *  is satisfied iff `p` points to `q`.
     * 
     * This means that a node `q` in `cg` will be in the returned set iff `cg` includes
     * a finite sequence of call edges between any node `m` in `init` and `n`.
     */
    public static IdentitySet<Object> makePointsToClosure(PointerKey ptr, HeapGraph hg)
    {
        IdentitySet<CGNode> closure = new IdentitySet<CGNode>();
        throw new UnsupportedOperationException();
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
