package org.paninij.soter;

import static java.io.File.pathSeparator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.paninij.soter.SoterAnalysis;
import org.paninij.soter.instrument.SoterInstrumenter;
import org.paninij.soter.instrument.SoterInstrumenterFactory;
import org.paninij.soter.util.WalaUtil;

import com.beust.jcommander.JCommander;


/**
 * This class defines a simple executable which analyzes capsule templates using the SOTER analysis
 * and instruments them accordingly.
 */
public class Main
{
    protected final CLIArguments cliArguments;
    protected final SoterAnalysisFactory soterAnalysisFactory;
    protected final SoterInstrumenterFactory soterInstrumenterFactory;
    
    protected Main(CLIArguments cliArguments)
    {
        // Note that instantiation of the analysis factory needs to happen after the artifacts have
        // been compiled so that the bytecode for those artifacts will be found by the CHA.
        this.cliArguments = cliArguments;

        String cp = makeEffectiveClassPath(cliArguments.classPath, cliArguments.classPathFile);
        note("Effective class path: " + cp);
        soterAnalysisFactory = new SoterAnalysisFactory(cp);
        soterInstrumenterFactory = new SoterInstrumenterFactory(cliArguments.classOutput);
    }
   
    protected void analyzeAndInstrument(String qualifiedCapsuleName)
    {
        note("Analyzing and Instrumenting Capsule: " + qualifiedCapsuleName);
        
        SoterAnalysis soterAnalysis;
        try {
            soterAnalysis = soterAnalysisFactory.make(qualifiedCapsuleName);
            soterAnalysis.perform();
        }
        catch (Exception ex)
        {
            error("Caught an exception while analyzing a capsule: " + qualifiedCapsuleName);
            ex.printStackTrace(System.err);
            return;
        }

        if (cliArguments.analysisReports != null)
        {
            log(cliArguments.analysisReports + File.separator + qualifiedCapsuleName,
                soterAnalysis.makeReport(), false);
        }
        
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

    public static void main(String[] args)
    {
        CLIArguments cliArguments = new CLIArguments();
        new JCommander(cliArguments, args);
        Main main = new Main(cliArguments);
        for (String capsuleTemplate : cliArguments.capsuleTemplates) {
            main.analyzeAndInstrument(capsuleTemplate);
        }
    }
    
    
    public void log(String logFilePath, String logMsg, boolean append)
    {
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, append))))
        {
            out.println(logMsg);
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Failed to log a message: " + ex, ex);
        }
    }
    
    public void note(String msg) {
        System.err.println("--- org.paninij.soter.Main: " + msg);
    }

    public void warning(String msg) {
        System.err.println("~~~ org.paninij.soter.Main: " + msg);
    }
    
    public void error(String msg) {
        System.err.println("!!! org.paninij.soter.Main: " + msg);
    }
}
