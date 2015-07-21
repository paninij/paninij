package org.paninij.apt.soter;

import static org.paninij.apt.util.ArtifactCompiler.buildEffectiveClassPath;

import org.junit.Test;
import org.paninij.soter.prototype.SoterAnalysisPrototype;


public class TestSoterPrototype
{
    private static final String CLASS_PATH_FILE = "target/generated-resources/maven/panini_processor_classpath.txt";
    private static final String CLASS_PATH = buildEffectiveClassPath("target/test-classes", CLASS_PATH_FILE);
    private static final String ACTIVE_CLIENT_PATH = "Lorg/paninij/apt/soter/ActiveClientTemplate";
    private static final String LEAKY_SERVER_PATH = "Lorg/paninij/apt/soter/LeakyServerTemplate";

    @Test
    public void analyzeActiveClient() throws Throwable
    {
        analyze(ACTIVE_CLIENT_PATH);
    }

    @Test
    public void analyzeLeakyServer() throws Throwable
    {
        analyze(LEAKY_SERVER_PATH);
    }
    
    private void analyze(String templatePath) throws Throwable
    {
        SoterAnalysisPrototype prototype = new SoterAnalysisPrototype(templatePath, CLASS_PATH);
        prototype.perform();
    }
}
