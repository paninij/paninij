package org.paninij.soter;

import java.io.IOException;

import org.junit.Test;

public class TestDriver
{
    String[] args = {"-classPath", "/Users/owl/Projects/Panini/at-paninij/at-paninij-analyses/at-paninij-soter-tests/target/classes",
                     "-classPathFile", "target/dependencies.txt",
                     "-classOutput", "target/classes",
                     "-analysisReports", "logs/soter/analysis_reports",
                     "-origBytecode", "logs/soter/orig_bytecode",
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
