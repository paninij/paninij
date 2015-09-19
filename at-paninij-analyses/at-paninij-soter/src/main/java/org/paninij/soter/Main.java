package org.paninij.soter;

import static java.io.File.pathSeparator;

import static org.paninij.soter.util.Log.error;
import static org.paninij.soter.util.Log.note;
import static org.paninij.soter.util.Log.warning;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.paninij.soter.SoterAnalysis;
import org.paninij.soter.instrument.SoterInstrumenter;
import org.paninij.soter.instrument.SoterInstrumenterFactory;
import org.paninij.soter.util.Log;
import org.paninij.soter.util.WalaUtil;

import com.beust.jcommander.JCommander;


/**
 * This class defines a simple executable which analyzes capsule templates using the SOTER analysis
 * and instruments them accordingly.
 */
public class Main
{
    protected final String classpath;
    protected final CLIArguments cliArguments;
    protected final SoterAnalysisFactory soterAnalysisFactory;
    protected final SoterInstrumenterFactory soterInstrumenterFactory;
    
    protected Main(CLIArguments cliArguments)
    {
        // Note that instantiation of the analysis factory needs to happen after the artifacts have
        // been compiled so that the bytecode for those artifacts will be found by the CHA.
        this.cliArguments = cliArguments;
        if (cliArguments.analysisReports != null) {
            Log.analysisLogDirectory = Paths.get(cliArguments.analysisReports);
        }

        classpath = makeEffectiveClassPath(cliArguments.classPath, cliArguments.classPathFile);
        note("Effective class path: " + classpath);
        soterAnalysisFactory = new SoterAnalysisFactory(classpath);
        soterInstrumenterFactory = new SoterInstrumenterFactory(cliArguments.classOutput);
    }
   
    // TODO: Split up this method. It's too long and does too much.
    protected void analyzeAndInstrument(String qualifiedCapsuleName) throws IOException,
                                                                     InterruptedException
    {
        note("Analyzing Capsule: " + qualifiedCapsuleName);
        
        SoterAnalysis soterAnalysis;
        try {
            soterAnalysis = soterAnalysisFactory.make(qualifiedCapsuleName);
            soterAnalysis.perform();
        }
        catch (Exception ex)
        {
            error("Caught an exception while analyzing a capsule: " + qualifiedCapsuleName);
            throw ex;
        }

        if (cliArguments.noInstrument == false)
        {
            note("Instrumenting Capsule: " + qualifiedCapsuleName);
            try {
                SoterInstrumenter soterInstrumenter = soterInstrumenterFactory.make(soterAnalysis);
                soterInstrumenter.perform();
            }
            catch (Exception ex)
            {
                error("Caught an exception while instrumenting a capsule: " + qualifiedCapsuleName);
                ex.printStackTrace(System.err);
                return;
            }
        }

        if (cliArguments.callGraphPDFs != null)
        {
            String callGraphPDF = cliArguments.callGraphPDFs + File.separator + qualifiedCapsuleName + ".pdf";
            WalaUtil.makeGraphFile(soterAnalysis.getCallGraph(), callGraphPDF);
        }

        if (cliArguments.heapGraphPDFs != null)
        {
            String heapGraphPDF = cliArguments.heapGraphPDFs + File.separator + qualifiedCapsuleName + ".pdf";
            WalaUtil.makeGraphFile(soterAnalysis.getHeapGraph(), heapGraphPDF);
        }
    }
    
    public void logAllTemplatesDisassembledBytecode() throws IOException, InterruptedException
    {
        for (String capsuleTemplate: cliArguments.capsules)
        {
            logDisassembledBytecode(capsuleTemplate + "Template",
                                    cliArguments.classPath,
                                    cliArguments.origBytecode);
        } 
    }
    
    // TODO: Move this helper method to somewhere else.
    protected static void logDisassembledBytecode(String qualifiedClassName, String classpath,
                                                  String directory)
                                                  throws IOException, InterruptedException
    {
        // TODO: BUG: Handle the case that `classpath` contains spaces!
        String cmd = "javap -c -classpath " + classpath + " " + qualifiedClassName;
        note(cmd);

        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(cmd);
        Path path = Paths.get(directory, qualifiedClassName);

        proc.waitFor();
        if (proc.exitValue() > 0)
        {
            warning("Attempt to obtain disassembled bytecode failed: " + qualifiedClassName);
            InputStream err = proc.getErrorStream();
            Files.copy(err, path, StandardCopyOption.REPLACE_EXISTING);
        }
        else
        {
            InputStream in = proc.getInputStream();
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
    /**
     * Appends the contents of the `classPathFile` to the given `classPath`. Either argument can
     * be `null`.
     * 
     * @throws IllegalArgumentException if the `classPathFile` could not be read.
     */
    public static String makeEffectiveClassPath(String classPath, String classPathFile)
    {
        if (classPath == null) {
            classPath = "";
        }
        if (classPathFile == null || classPathFile == "") {
            return classPath;
        }
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(classPathFile));
            String contents = new String(bytes, "UTF-8");
            if (! contents.isEmpty()) {
                classPath = (classPath.equals("")) ? contents : classPath + pathSeparator + contents;
            }
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Could not read `classPathFile`: " + classPathFile);
        }
    
        return classPath;
    }

    public static void main(String[] args) throws IOException, InterruptedException
    {
        CLIArguments cliArguments = new CLIArguments();
        new JCommander(cliArguments, args);
        Main main = new Main(cliArguments);
        
        if (cliArguments.origBytecode != null) {
            main.logAllTemplatesDisassembledBytecode();
        }
        
        for (String capsule : cliArguments.capsules) {
            main.analyzeAndInstrument(capsule);
        }
    }

}
