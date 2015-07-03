package org.paninij.soter;

import org.junit.Test;

public class TestMain
{
    @Test
    public void testMainWithLeakyServer()
    {
        String[] args = {"-classpath", "target/classes", "org/paninij/soter/LeakyServerTemplate"};
        Main.main(args);
    }
}
