package org.paninij.soter.experiments;

import org.junit.Test;
import org.paninij.soter.PaniniAnalysis;
import org.paninij.soter.util.WalaDebug;


public class TestCallGraph
{
    private static final String CLASSPATH = "target/classes:target/test-classes"; 
    
    @Test
    public void testCallGraphWithLeakyServer() throws Throwable
    {
        makeCallGraph("Lorg/paninij/soter/LeakyServerTemplate", CLASSPATH, "LeakyServerCallGraph.pdf");
    }
    

    @Test
    public void testCallGraphWithActiveClient() throws Throwable
    {
        makeCallGraph("Lorg/paninij/soter/ActiveClientTemplate", CLASSPATH, "ActiveClientCallGraph.pdf");
    }
    

    private void makeCallGraph(String template, String classPath, String pdfName) throws Throwable
    {
        PaniniAnalysis analysis = new PaniniAnalysis(template, classPath);
        analysis.perform();
        WalaDebug.makeGraphFile(analysis.getCallGraph(), pdfName);
    }
}
