package org.paninij.soter2.tests;

import static org.paninij.apt.util.PaniniArtifactCompiler.buildEffectiveClassPath;

import org.junit.Test;
import org.paninij.runtime.util.IdentitySet;
import org.paninij.soter.util.SoterUtil;
import org.paninij.soter2.PaniniCallGraphBuilder;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;

public class Scratch
{
    private static final String CLASSPATH_FILE = "target/generated-resources/maven/panini_processor_classpath.txt";
    private static final String CLASSPATH = buildEffectiveClassPath("target/test-classes", CLASSPATH_FILE);

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
