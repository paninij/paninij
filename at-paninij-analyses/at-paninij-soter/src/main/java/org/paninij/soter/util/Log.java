package org.paninij.soter.util;

import java.nio.file.Path;


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
