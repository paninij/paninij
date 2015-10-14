package org.paninij.soter;

import static java.util.Collections.singleton;
import static org.paninij.proc.driver.ProcDriver.makeDefaultSettings;

import org.junit.Test;

import org.paninij.proc.driver.ProcDriver;
import org.paninij.proc.driver.ProcDriver.Settings;


public class TestApsp
{
    String[] templates = {
        "edu.rice.habanero.benchmarks.apsp.MasterTemplate",
        "edu.rice.habanero.benchmarks.apsp.WorkerTemplate",
        "edu.rice.habanero.benchmarks.apsp.ApspTemplate"
    };

    String[] soterArgs = {
        "-classPath", "target/classes",
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
        ProcDriver driver = new ProcDriver(makeSettings());
        driver.process(templates);
        org.paninij.soter.Main.main(soterArgs);
    }
    

    private static Settings makeSettings()
    {
        Settings settings = makeDefaultSettings();
        settings.options = singleton("-Apanini.exceptOnFailedChecks");
        return settings;
    }
}
