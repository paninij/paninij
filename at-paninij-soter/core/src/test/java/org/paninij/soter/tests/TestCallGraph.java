package org.paninij.soter.tests;

import org.junit.Test;
import org.paninij.soter.PaniniAnalysis;
import org.paninij.soter.util.WalaUtil;


public class TestCallGraph
{
    private static final String CLASSPATH = "lib/at-paninij-runtime.jar:target/classes:target/test-classes"; 
    
    @Test
    public void testCallGraphWithLeakyServer() throws Throwable
    {
        makeCallGraph("Lorg/paninij/soter/tests/LeakyServerTemplate", CLASSPATH, "logs/LeakyServerCallGraph.pdf");
    }
    

    @Test
    public void testCallGraphWithActiveClient() throws Throwable
    {
        makeCallGraph("Lorg/paninij/soter/tests/ActiveClientTemplate", CLASSPATH, "logs/ActiveClientCallGraph.pdf");
    }
    

    private void makeCallGraph(String template, String classPath, String pdfName) throws Throwable
    {
        PaniniAnalysis analysis = new PaniniAnalysis(template, classPath);
        analysis.perform();
        WalaUtil.makeGraphFile(analysis.getCallGraph(), pdfName);
    }
}
