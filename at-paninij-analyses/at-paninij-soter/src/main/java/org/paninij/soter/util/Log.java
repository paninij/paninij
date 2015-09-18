package org.paninij.soter.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Log
{
    private Log() {
        // Can't instantiate class.
    }

 
    /**
     * Indicates the directory under which SOTER analysis logs should be placed. If this is set to
     * `null`, then no SOTER analysis logs should be generated.
     */
    public static Path analysisLogDirectory = null;

   
    /**
     * Logs the given `msg` in a file named `fileName` under the directory with the given
     * `analysisName` within the `analysisLogDirectory`. Note that this method does nothing if
     * `analysisLogDirectory` is not set.
     */
    public static void logAnalysis(String analysisName, String fileName, String msg, boolean append)
    {
        if (analysisLogDirectory == null) {
            return;
        }

        Path logFilePath = Paths.get(analysisLogDirectory.toString(), analysisName, fileName);
        note("Logging analysis: " + logFilePath);

        try
        {
            Files.createDirectories(logFilePath.getParent());

            FileWriter fw = new FileWriter(logFilePath.toFile(), append);
            PrintWriter out = new PrintWriter(new BufferedWriter(fw));
            out.println(msg);
            fw.close();
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Failed to log an analysis: " + ex, ex);
        }
    }
    

    public static void note(String msg) {
        System.err.println("--- " + msg);
    }


    public static void warning(String msg) {
        System.err.println("~~~ " + msg);
    }

    
    public static void error(String msg) {
        System.err.println("!!! " + msg);
    }
}
