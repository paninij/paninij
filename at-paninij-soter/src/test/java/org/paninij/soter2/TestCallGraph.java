package org.paninij.soter2;

import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;
import org.paninij.soter.PaniniAnalysis;
import org.paninij.soter.util.WalaDebug;

import com.ibm.wala.ipa.callgraph.CallGraph;


public class TestCallGraph
{
    private static final String CLASSPATH = "target/classes:target/test-classes"; 
    
    @Test
    public void testCallGraphWithLeakyServer() throws Throwable {
        makeCallGraph("Lorg/paninij/soter/LeakyServerTemplate", CLASSPATH, "LeakyServerCallGraph.pdf");
    }
    
    @Test
    public void testCallGraphWithActiveClient() throws Throwable {
        makeCallGraph("Lorg/paninij/soter/ActiveClientTemplate", CLASSPATH, "ActiveClientCallGraph.pdf");
    }

    private void makeCallGraph(String template, String classPath, String pdfName) throws Throwable
    {
        PaniniTemplateZeroOneCFA cfa = PaniniTemplateZeroOneCFA.make(template, classPath);
        cfa.perform();
        Consumer<CallGraph> makeCallGraphFile = (cg -> WalaDebug.makeGraphFile(cg, pdfName));
        cfa.acceptUponCallGraph(makeCallGraphFile);
    }
}
