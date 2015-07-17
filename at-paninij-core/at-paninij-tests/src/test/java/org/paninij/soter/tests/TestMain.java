package org.paninij.soter.tests;

import static org.paninij.apt.util.PaniniArtifactCompiler.buildEffectiveClassPath;

import org.junit.Test;
import org.paninij.soter.Main;

public class TestMain
{
    private static final String CLASSPATH_FILE = "target/generated-resources/maven/panini_processor_classpath.txt";
    private static final String CLASSPATH = buildEffectiveClassPath("target/test-classes", CLASSPATH_FILE);

    @Test
    public void testMainWithLeakyServer() throws Exception
    {
        String[] args = {"-classpath", CLASSPATH, "Lorg/paninij/soter/tests/LeakyServerTemplate"};
        Main.main(args);
    }
    
    @Test
    public void testMainWithActiveClient() throws Exception
    {
        String[] args = {"-classpath", CLASSPATH, "Lorg/paninij/soter/tests/ActiveClientTemplate"};
        Main.main(args);
    }
}
