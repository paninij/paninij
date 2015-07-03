package org.paninij.soter;

import org.junit.Test;

public class TestMain
{
    @Test
    public void testMainWithLeakyServer() throws Exception
    {
        String[] args = {"-classpath", "target/test-classes",
                         "Lorg/paninij/soter/LeakyServerTemplate"};
        Main.main(args);
    }
    
    @Test
    public void testMainWithActiveClient() throws Exception
    {
        String[] args = {"-classpath", "target/test-classes",
                         "Lorg/paninij/soter/ActiveClientTemplate"};
        Main.main(args);
    }

}
