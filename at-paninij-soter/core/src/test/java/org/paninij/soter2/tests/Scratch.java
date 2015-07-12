package org.paninij.soter2.tests;

import org.junit.Test;
import org.paninij.runtime.util.IdentitySet;
import org.paninij.soter.util.PaniniModel;
import org.paninij.soter.util.SoterUtil;
import org.paninij.soter2.PaniniCallGraphBuilder;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;

public class Scratch
{
    private static final String CLASSPATH = "lib/at-paninij-soter-tests.jar:lib/at-paninij-runtime.jar"; 
    private static final String ACTIVE_CLIENT_NAME = "Lorg/paninij/soter/tests/ActiveClientTemplate";
    
    @Test
    public void scratch()
    {
        PaniniCallGraphBuilder built = PaniniCallGraphBuilder.build(ACTIVE_CLIENT_NAME, CLASSPATH);
        CallGraph cg = built.getCallGraph();
        IdentitySet<CGNode> init = SoterUtil.getInitialCallNodes(cg);
        IdentitySet<CGNode> reachable = SoterUtil.makeCalledByClosure(init, cg);

        System.out.println("[`init` nodes]");
        for (CGNode n : init) {
            System.out.println(n);
        }

        System.out.println("[`reachable` nodes]");
        for (CGNode n : reachable) {
            System.out.println(n);
        }
    }
}
