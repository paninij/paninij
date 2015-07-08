package org.paninij.soter.tests;

import org.junit.Test;
import org.paninij.soter.Main;

public class TestMain
{
    @Test
    public void testMainWithLeakyServer() throws Exception
    {
        String[] args = {"-classpath", "target/classes:target/test-classes",
                         "Lorg/paninij/soter/LeakyServerTemplate"};
        Main.main(args);
    }
    
    @Test
    public void testMainWithActiveClient() throws Exception
    {
        String[] args = {"-classpath", "target/classes:target/test-classes",
                         "Lorg/paninij/soter/ActiveClientTemplate"};
        Main.main(args);
    }

}
