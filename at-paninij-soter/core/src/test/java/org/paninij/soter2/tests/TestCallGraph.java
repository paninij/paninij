package org.paninij.soter2.tests;

import org.junit.Test;
import org.paninij.soter.util.WalaDebug;
import org.paninij.soter2.NoisyPaniniCallGraphBuilder;
import org.paninij.soter2.PaniniCallGraphBuilder;

import com.ibm.wala.analysis.pointers.HeapGraph;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;


public class TestCallGraph
{
    private static final String CLASSPATH = "lib/at-paninij-soter-tests.jar:lib/at-paninij-runtime.jar"; 
    
    @Test
    public void testCallGraphWithLeakyServer() throws Throwable {
        makeCallGraph("Lorg/paninij/soter/tests/LeakyServerTemplate", CLASSPATH,
                      "logs/LeakyServerCallGraph.pdf", "logs/LeakyServerHeapGraph.pdf");
    }
    
    @Test
    public void testCallGraphWithActiveClient() throws Throwable {
        makeCallGraph("Lorg/paninij/soter/tests/ActiveClientTemplate", CLASSPATH,
                      "logs/ActiveClientCallGraph.pdf", "logs/ActiveClientHeapGraph.pdf");
    }

    private void makeCallGraph(String template, String classPath,
                               String callGraphPDF, String heapGraphPDF) throws Throwable
    {
        PaniniCallGraphBuilder cfaBuilder = NoisyPaniniCallGraphBuilder.make(template, classPath);

        CallGraph cg = cfaBuilder.makeCallGraph();
        HeapGraph<InstanceKey> hg = cfaBuilder.getHeapGraph();

        WalaDebug.makeGraphFile(cg, callGraphPDF);
        WalaDebug.makeGraphFile(hg, heapGraphPDF);
    }
}
