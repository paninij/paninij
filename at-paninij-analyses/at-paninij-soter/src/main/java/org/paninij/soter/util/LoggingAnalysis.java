package org.paninij.soter.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.json.JsonObject;

public abstract class LoggingAnalysis extends Analysis
{
    @Override
    public void perform()
    {
        if (hasBeenPerformed) {
            return;
        }
        performSubAnalyses();
        performAnalysis();
        hasBeenPerformed = true;
        logJsonResults();
        assert checkPostConditions();
    }
   

    public abstract JsonObject getJsonResults();


    public abstract String getJsonResultsString();


    public void logJsonResults()
    {
        String analysisName = getClass().getSimpleName();
        logAnalysis(analysisName, getJsonResultsLogFileName(), getJsonResultsString(), false);
    }
    

    protected abstract String getJsonResultsLogFileName();
        
    
    /**
     * Logs the given `msg` in a file named `fileName` under the directory with the given
     * `analysisName` within the `analysisLogDirectory`. Note that this method does nothing if
     * `analysisLogDirectory` is not set.
     */
    public static void logAnalysis(String analysisName, String fileName, String msg, boolean append)
    {
        if (Log.analysisLogDirectory == null) {
            return;
        }

        Path logFilePath = Paths.get(Log.analysisLogDirectory.toString(), analysisName, fileName);
        Log.note("Logging Analysis: " + logFilePath);

        try
        {
            Files.createDirectories(logFilePath.getParent());

            FileWriter fw = new FileWriter(logFilePath.toFile(), append);
            PrintWriter out = new PrintWriter(new BufferedWriter(fw));
            out.println(msg);
            out.close();
            fw.close();
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Failed to log an analysis: " + ex, ex);
        }
    }
}
