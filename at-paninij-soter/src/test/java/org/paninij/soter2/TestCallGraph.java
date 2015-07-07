package org.paninij.soter2;

import java.util.function.Consumer;

import org.junit.Test;
import org.paninij.soter.util.WalaDebug;

import com.ibm.wala.analysis.pointers.HeapGraph;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;


public class TestCallGraph
{
    private static final String CLASSPATH = "target/classes:target/test-classes"; 
    
    @Test
    public void testCallGraphWithLeakyServer() throws Throwable {
        makeCallGraph("Lorg/paninij/soter/LeakyServerTemplate", CLASSPATH,
                      "LeakyServerCallGraph.pdf", "LeakyServerHeapGraph.pdf");
    }
    
    @Test
    public void testCallGraphWithActiveClient() throws Throwable {
        makeCallGraph("Lorg/paninij/soter/ActiveClientTemplate", CLASSPATH,
                      "ActiveClientCallGraph.pdf", "ActiveClientHeapGraph.pdf");
    }

    private void makeCallGraph(String template, String classPath,
                               String callGraphPDF, String heapGraphPDF) throws Throwable
    {
        PaniniTemplateZeroOneCFA cfa = PaniniTemplateZeroOneCFA.make(template, classPath);
        cfa.perform();

        Consumer<CallGraph> makeCallGraph = (cg -> WalaDebug.makeGraphFile(cg, callGraphPDF));
        Consumer<HeapGraph<InstanceKey>> makeHeapGraph = (hg -> WalaDebug.makeGraphFile(hg, heapGraphPDF));

        cfa.acceptUponCallGraph(makeCallGraph);
        cfa.acceptUponHeapGraph(makeHeapGraph);
    }
}
