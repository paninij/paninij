package org.paninij.soter;

import org.junit.Test;

public class TestApsp
{
    String[] args = {"-classPath", "target/classes",
            "-classPathFile", "target/dependencies.txt",
            "-classOutput", "target/classes",
            "-analysisReports", "logs/analysis_reports",
            "-originalBytecode", "logs/original_bytecode",
            "-instrumentedBytecode", "logs/instrumented_bytecode",
            "edu.rice.habanero.benchmarks.apsp.Master",
            "edu.rice.habanero.benchmarks.apsp.Worker",
            "edu.rice.habanero.benchmarks.apsp.Apsp"
    };

    /**
    * Tries running the soter analysis (with instrumentation) to see if it crashes.
    * @throws Exception 
    */
    @Test
    public void instrumentationSmokeTest() throws Exception
    {
        //org.paninij.soter.Main.main(args);
        //edu.rice.habanero.benchmarks.apsp.Apsp$Thread.main(null);
    }
}
