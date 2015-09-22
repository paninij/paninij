package org.paninij.soter.cli;

import static java.io.File.separator;

import static org.paninij.soter.util.Log.error;
import static org.paninij.soter.util.Log.note;

import java.nio.file.Paths;

import org.paninij.soter.SoterAnalysis;
import org.paninij.soter.SoterAnalysisFactory;
import org.paninij.soter.instrument.SoterInstrumenter;
import org.paninij.soter.instrument.TemplateInstrumenterFactory;
import org.paninij.soter.util.Command;
import org.paninij.soter.util.Log;
import org.paninij.soter.util.WalaUtil;

import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;


/**
 * This class defines a simple executable which analyzes capsule templates using the SOTER analysis
 * and instruments them accordingly.
 */
public class SoterCommand extends Command
{
    protected final CLIArguments cliArguments;
    protected final String classpath;
    protected final SoterAnalysisFactory soterAnalysisFactory;
    protected final TemplateInstrumenterFactory templateInstrumenterFactory;
    
    public SoterCommand(CLIArguments cliArguments)
    {
        // Note that instantiation of the analysis factory needs to happen after the artifacts have
        // been compiled so that the bytecode for those artifacts will be found by the CHA.
        this.cliArguments = cliArguments;
        if (cliArguments.analysisReports != null) {
            Log.analysisLogDirectory = Paths.get(cliArguments.analysisReports);
        }

        classpath = Util.makeEffectiveClassPath(cliArguments.classPath, cliArguments.classPathFile);
        note("Effective class path: " + classpath);
        soterAnalysisFactory = new SoterAnalysisFactory(classpath);
        templateInstrumenterFactory = new TemplateInstrumenterFactory(cliArguments.classOutput);
    }

    @Override
    protected void performCommand() throws Exception
    {
        if (cliArguments.origBytecode != null) {
            Util.logAllTemplatesDisassembledBytecode(cliArguments.capsules, cliArguments.classPath,
                                                     cliArguments.origBytecode);
        }
        
        for (String capsule : cliArguments.capsules) {
            analyzeAndInstrument(capsule);
        }
    }
    

    // TODO: Split up this method. It's too long and does too much.
    protected void analyzeAndInstrument(String qualifiedCapsuleName) throws Exception
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
                ClassInstrumenter templateInstrumenter = templateInstrumenterFactory.make(soterAnalysis.getCapsuleTemplate());
                SoterInstrumenter soterInstrumenter = new SoterInstrumenter(templateInstrumenter,
                                                                            soterAnalysis,
                                                                            cliArguments.classOutput);
                soterInstrumenter.perform();
            }
            catch (Exception ex)
            {
                String msg = "Caught an exception while instrumenting a capsule: " + qualifiedCapsuleName;
                error(msg);
                throw new RuntimeException (msg, ex);
            }
        }

        if (cliArguments.callGraphPDFs != null)
        {
            String callGraphPDF = cliArguments.callGraphPDFs + separator + qualifiedCapsuleName + ".pdf";
            WalaUtil.makeGraphFile(soterAnalysis.getCallGraph(), callGraphPDF);
        }

        if (cliArguments.heapGraphPDFs != null)
        {
            String heapGraphPDF = cliArguments.heapGraphPDFs + separator + qualifiedCapsuleName + ".pdf";
            WalaUtil.makeGraphFile(soterAnalysis.getHeapGraph(), heapGraphPDF);
        }
    }

}
