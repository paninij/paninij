package org.paninij.soter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
        System.err.println("compilerClassPath: " + cliArguments.compilerClassPath);
        soterAnalysisFactory = new SoterAnalysisFactory(cliArguments.compilerClassPath);
        soterInstrumenterFactory = new SoterInstrumenterFactory(cliArguments.classOutput);
    }
   
    protected void analyzeAndInstrument(String qualifiedCapsuleName)
    {
        SoterAnalysis soterAnalysis = soterAnalysisFactory.make(qualifiedCapsuleName);
        soterAnalysis.perform();

        if (cliArguments.analysisReports != null)
        {
            log(cliArguments.analysisReports + File.separator + qualifiedCapsuleName,
                soterAnalysis.getResultsReport(), false);
        }

        SoterInstrumenter soterInstrumenter = soterInstrumenterFactory.make(soterAnalysis);
        soterInstrumenter.perform();

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
}
