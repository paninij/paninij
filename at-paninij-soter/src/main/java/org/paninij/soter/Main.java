package org.paninij.soter;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.beust.jcommander.JCommander;

/**
 * 
 * @author David Johnston
 *
 */
public class Main
{
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args)
    {
        logger.info("Starting up...");

        AnalysisTasks tasks = new AnalysisTasks();
        new JCommander(tasks, args);

        tasks.log(Level.INFO);
    }
}
