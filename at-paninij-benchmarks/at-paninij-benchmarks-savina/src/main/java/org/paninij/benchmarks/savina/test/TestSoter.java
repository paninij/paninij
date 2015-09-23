package org.paninij.benchmarks.savina.test;

import java.io.IOException;

import org.junit.Test;

public class TestSoter
{
    String[] args = {"-classPath", "target/classes",
                     "-classPathFile", "target/dependencies.txt",
                     "-classOutput", "target/classes",
                     "-analysisReports", "logs/soter/analysis_reports",
                     "-originalBytecode", "logs/soter/original_bytecode",
                     "-instrumentedBytecode", "logs/soter/instrumented_bytecode",
                     "-noInstrument",
                     "@target/capsule_list.txt"
    };
    
    /**
     * Tries running the soter analysis (without instrumentation) to see if it crashes.
     * @throws IOException 
     */
    @Test
    public void noInstrumentationSmokeTest() throws Exception
    {
        org.paninij.soter.Main.main(args);
    }
}