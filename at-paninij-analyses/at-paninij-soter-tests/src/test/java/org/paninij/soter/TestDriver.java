package org.paninij.soter;

import java.io.IOException;

import org.junit.Test;

public class TestDriver
{
    String[] args = {"-classPath", "target/classes",
                     "-classPathFile", "target/dependencies.txt",
                     "-classOutput", "target/classes",
                     "-analysisReports", "logs/analysis_reports",
                     "-origBytecode", "logs/orig_bytecode",
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