package org.paninij.lang;

import static org.paninij.lang.ExecutionProfile.*;

import java.lang.String;  // Needed to prevent unintended use of `org.paninij.lang.String`.

import org.junit.Test;


public class TestSystem
{
    private static String PACKAGE_PREFIX = "org.paninij.lang.helloworld.";
    private static ExecutionProfile[] RUNNABLE_EXECUTION_PROFILES = {
        MONITOR,
        SERIAL,
        TASK,
        THREAD,
    };
    
    @Test
    public void testHelloWorld$Thread() {
        run("HelloWorld", THREAD);
    }
    
    @Test
    public void testHelloWorldShort$Thread() {
        run("HelloWorldShort", THREAD);
    }
    
    @Test
    public void testHelloWorld$All() {
        runWithEachProfile("HelloWorld");
    }
    
    @Test
    public void testHelloWorldShort$All() {
        runWithEachProfile("HelloWorldShort");
    }
    
    @Test
    public void testCLI() {
        CapsuleSystem.main(new String[] {PACKAGE_PREFIX + "HelloWorldShort"});
    }
    

    private static void runWithEachProfile(String capsuleName)
    {
        for (ExecutionProfile profile : RUNNABLE_EXECUTION_PROFILES) {
            run(capsuleName, profile);
        }
    }
    
    private static void run(String capsuleName, ExecutionProfile profile) {
        CapsuleSystem.start(PACKAGE_PREFIX + capsuleName, profile, new String[] { /* no args */ });
    }
}
