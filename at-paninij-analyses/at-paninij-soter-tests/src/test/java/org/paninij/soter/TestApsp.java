package org.paninij.soter;

import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static org.paninij.proc.driver.ProcDriver.makeDefaultSettings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
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

    private ProcDriver driver;
    
    @Before
    public void setUp() throws IOException {
        driver = new ProcDriver(makeSettings());
    }
    
    /**
    * Tries running the soter analysis (with instrumentation) to see if it crashes.
    * @throws Exception 
    */
    @Test
    public void instrumentationSmokeTest() throws Exception
    {
        driver.process(templates);
        org.paninij.soter.Main.main(soterArgs);
        //edu.rice.habanero.benchmarks.apsp.Apsp$Thread.main(null);
    }
    
    private static Settings makeSettings()
    {
        List<File> classPath = new ArrayList<File>();
        for (String s : asList(getProperty("java.class.path").split(File.pathSeparator))) {
            classPath.add(new File(s));
        }
        List<File> sourcePath = asList(
            new File("src/main/java"),
            new File("src/main/at-paninij"),
            new File("target/generated-sources")
        );
        File sourceOutput = new File("target/generated-sources");
        File classOutput = new File("target/classes");
        List<String> options = asList("-Apanini.exceptOnFailedChecks");

        return new Settings(classPath, sourcePath, classOutput, sourceOutput, options);
    }


}
