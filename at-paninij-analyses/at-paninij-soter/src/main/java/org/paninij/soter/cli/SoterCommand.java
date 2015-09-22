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
    protected final CLIArguments cliArgs;
    protected final String classpath;
    protected final SoterAnalysisFactory soterAnalysisFactory;
    
    public SoterCommand(CLIArguments cliArgs)
    {
        // Note that instantiation of the analysis factory needs to happen after the artifacts have
        // been compiled so that the bytecode for those artifacts will be found by the CHA.
        this.cliArgs = cliArgs;
        if (cliArgs.analysisReports != null) {
            Log.analysisLogDirectory = Paths.get(cliArgs.analysisReports);
        }

        classpath = Util.makeEffectiveClassPath(cliArgs.classPath, cliArgs.classPathFile);
        note("Effective class path: " + classpath);
        soterAnalysisFactory = new SoterAnalysisFactory(classpath);
    }

    @Override
    protected void performCommand() throws Exception
    {
        for (String qualifiedCapsuleName : cliArgs.capsules)
        {
            // TODO: Check that `qualifiedCapsuleName` is valid.
            // TODO: Change `qualifiedCapsuleName` so that it is encapsulated by a type.
            logOrigBytecode(qualifiedCapsuleName);
            SoterAnalysis soterAnalysis = analyzeCapsule(qualifiedCapsuleName);
            instrumentCapsule(qualifiedCapsuleName, soterAnalysis);
            logGraphPDFs(qualifiedCapsuleName, soterAnalysis);
        }
    }
    
    
    protected void logOrigBytecode(String qualifiedCapsuleName) throws Exception
    {
        try
        {
            if (cliArgs.origBytecode != null)
            {
                String templateName = qualifiedCapsuleName + "Template";
                note("Logging Template's Original Bytecode: " + templateName);
                Util.logDisassembledBytecode(templateName, cliArgs.classPath, cliArgs.origBytecode);
            }
        }
        catch (Exception ex)
        {
            error("Caught an exception while analyzing a capsule: " + qualifiedCapsuleName);
            throw ex;
        }
    }
    
    
    protected SoterAnalysis analyzeCapsule(String qualifiedCapsuleName)
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
        return soterAnalysis;
    }
    

    // TODO: Split up this method. It's too long and does too much.
    protected void instrumentCapsule(String qualifiedCapsuleName, SoterAnalysis soterAnalysis)
                                                                              throws Exception
    {
        if (cliArgs.noInstrument && cliArgs.instrumentAll) {
            String msg = "Cannot set both `-noInstrument` and `-instrumentAll`";
            throw new IllegalArgumentException(msg);
        }

        if (cliArgs.noInstrument) {
            return;  /* Nothing to do. */
        }

        ClassInstrumenter templateInstrumenter = TemplateInstrumenterFactory.make(
                                                     soterAnalysis.getCapsuleTemplate(),
                                                     cliArgs.classOutput
                                                 );
        if (cliArgs.instrumentAll)
        {
            note("Instrumenting Capsule (All Transferring Sites): " + qualifiedCapsuleName);
            throw new UnsupportedOperationException("TODO");
        }
        else
        {
            note("Instrumenting Capsule (SOTER Analysis): " + qualifiedCapsuleName);
            try {
                SoterInstrumenter soterInstrumenter = new SoterInstrumenter(templateInstrumenter,
                                                                            soterAnalysis,
                                                                            cliArgs.classOutput);
                soterInstrumenter.perform();
            }
            catch (Exception ex)
            {
                String msg = "Caught an exception while instrumenting a capsule: " + qualifiedCapsuleName;
                error(msg);
                throw new RuntimeException (msg, ex);
            }
        }
    }

    protected void logGraphPDFs(String qualifiedCapsuleName, SoterAnalysis sa)
    {
        if (cliArgs.callGraphPDFs != null)
        {
            note("Logging Call Graph: " + qualifiedCapsuleName);
            String callGraphPDF = cliArgs.callGraphPDFs + separator + qualifiedCapsuleName + ".pdf";
            WalaUtil.makeGraphFile(sa.getCallGraph(), callGraphPDF);
        }

        if (cliArgs.heapGraphPDFs != null)
        {
            note("Logging Heap Graph: " + qualifiedCapsuleName);
            String heapGraphPDF = cliArgs.heapGraphPDFs + separator + qualifiedCapsuleName + ".pdf";
            WalaUtil.makeGraphFile(sa.getHeapGraph(), heapGraphPDF);
        }       
    }
}
