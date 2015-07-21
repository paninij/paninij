package org.paninij.soter.prototype;

import static org.paninij.apt.util.ArtifactCompiler.buildEffectiveClassPath;

import org.junit.Test;
import org.paninij.soter.prototype.SoterAnalysisPrototype;
import org.paninij.soter.util.WalaUtil;


public class TestSoterPrototype
{
    private static final String CLASS_PATH_FILE = "target/generated-resources/maven/panini_processor_classpath.txt";
    private static final String CLASS_PATH_PREFIX = "target/test-classes";
    //private static final String CLASS_PATH_PREFIX = "target/classes:target/test-classes";
    private static final String CLASS_PATH = buildEffectiveClassPath(CLASS_PATH_PREFIX, CLASS_PATH_FILE);
    private static final String ACTIVE_CLIENT_PATH = "Lorg/paninij/apt/soter/ActiveClientTemplate";
    private static final String LEAKY_SERVER_PATH = "Lorg/paninij/apt/soter/LeakyServerTemplate";
    
    private static final String GRAPH_OUT_DIR = "logs/prototype/";

    @Test
    public void analyzeActiveClient() throws Throwable
    {
        analyze(ACTIVE_CLIENT_PATH, "ActiveClient");
    }

    @Test
    public void analyzeLeakyServer() throws Throwable
    {
        analyze(LEAKY_SERVER_PATH, "LeakyServer");
    }
    
    private void analyze(String templatePath, String shortName) throws Throwable
    {
        SoterAnalysisPrototype prototype = new SoterAnalysisPrototype(templatePath, CLASS_PATH);
        prototype.perform();
        WalaUtil.makeGraphFile(prototype.getCallGraph(), GRAPH_OUT_DIR + "/" + shortName + ".cg.pdf");
        WalaUtil.makeGraphFile(prototype.getHeapGraph(), GRAPH_OUT_DIR + "/" + shortName + ".hg.pdf");
    }
}
