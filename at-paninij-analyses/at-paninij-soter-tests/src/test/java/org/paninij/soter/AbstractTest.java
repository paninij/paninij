package org.paninij.soter;

import static java.util.Collections.singleton;
import static org.paninij.proc.driver.ProcDriver.makeDefaultSettings;

import org.paninij.proc.driver.ProcDriver;
import org.paninij.proc.driver.ProcDriver.Settings;


public abstract class AbstractTest
{
    /**
     * @return  The list of (fully qualified) capsule names to be tested.
     */
    protected abstract String[] getCapsules();
    

    /**
     * @return  An array of (fully qualified) capsule template names to be tested.
     */
    protected String[] getCapsuleTemplates()
    {
        String[] capsules = getCapsules();
        String[] templates = new String[capsules.length];
        for (int idx = 0; idx < capsules.length; idx++) {
            templates[idx] = capsules[idx] + "Template";
        }
        return templates;
    }
    
    
    /**
     * @return  The `ProcDriver.Settings` to be used when invoking `proc`.
     */
    protected Settings makeProcSettings()
    {
        Settings settings = makeDefaultSettings();
        settings.options = singleton("-Apanini.exceptOnFailedChecks");
        return settings;
    }


    /**
     * @return  An array of CLI arguments to the soter tool preceding the list of capsules to be
     *          analyzed and processed.
     */
    protected String[] getBaseSoterArgs()
    {
        return new String[] {
            "-classPath", "target/classes",
            "-classPathFile", "target/dependencies.txt",
            "-classOutput", "target/classes",
            "-analysisReports", "logs/analysis_reports",
            "-originalBytecode", "logs/original_bytecode",
            "-instrumentedBytecode", "logs/instrumented_bytecode"
        };
    }
    

    /**
     * @return  An array of all CLI arguments to the soter tool.
     */
    protected String[] getSoterArgs()
    {
       String[] baseArgs = getBaseSoterArgs();
       String[] capsules = getCapsules();

       String[] args = new String[baseArgs.length + capsules.length];
       for (int idx = 0; idx < baseArgs.length; idx++) {
           args[idx] = baseArgs[idx];
       }
       for (int idx = 0; idx < capsules.length; idx++) {
           args[idx+baseArgs.length] = capsules[idx];
       }

       return args;
    }
    
    
    /**
     * A basic test which triggers `proc`, runs the soter analysis, and performs instrumentation to
     * see whether these operations run to completion.
     * 
     * @throws Exception
     */
    public abstract void smokeTest() throws Exception;
    
    protected void defaultSmokeTest() throws Exception
    {
        ProcDriver driver = new ProcDriver(makeProcSettings());
        driver.process(getCapsuleTemplates());
        org.paninij.soter.Main.main(getSoterArgs()); 
    }
}
