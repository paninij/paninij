package org.paninij.soter2.tests;

import static org.paninij.apt.util.PaniniArtifactCompiler.buildEffectiveClassPath;

import org.junit.Test;
import org.paninij.soter.PaniniCallGraphBuilder;
import org.paninij.soter.util.WalaUtil;

import com.ibm.wala.analysis.pointers.HeapGraph;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;


public class TestCallGraph
{
    private static final String CLASSPATH_FILE = "target/generated-resources/maven/panini_processor_classpath.txt";
    private static final String CLASSPATH = buildEffectiveClassPath("target/test-classes", CLASSPATH_FILE);
    
    @Test
    public void testCallGraphWithLeakyServer() throws Throwable {
        logCallGraph("Lorg/paninij/soter/tests/LeakyServerTemplate", CLASSPATH,
                     "logs/LeakyServerCallGraph.pdf", "logs/LeakyServerHeapGraph.pdf");
    }
    
    @Test
    public void testCallGraphWithActiveClient() throws Throwable {
        logCallGraph("Lorg/paninij/soter/tests/ActiveClientTemplate", CLASSPATH,
                     "logs/ActiveClientCallGraph.pdf", "logs/ActiveClientHeapGraph.pdf");
    }

    private void logCallGraph(String template, String classPath,
                               String callGraphPDF, String heapGraphPDF) throws Throwable
    {
        PaniniCallGraphBuilder cfaBuilder = PaniniCallGraphBuilder.build(template, classPath);

        CallGraph cg = cfaBuilder.getCallGraph();
        HeapGraph<InstanceKey> hg = cfaBuilder.getHeapGraph();

        WalaUtil.makeGraphFile(cg, callGraphPDF);
        WalaUtil.makeGraphFile(hg, heapGraphPDF);
    }
}
