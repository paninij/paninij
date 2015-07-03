package org.paninij.soter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.beust.jcommander.Parameter;

public class AnalysisTasks
{
    private static final Logger logger = Logger.getLogger(AnalysisTasks.class.getName());
    
    @Parameter(names = "-classpath", description = "Classpath")
    public String classpath;
    
    @Parameter(description = "Capsules")
    public final List<String> capsules = new ArrayList<String>();
    
    public void log(Level level)
    {
        Object[] log_args = { classpath, capsules };
        logger.log(level, "AnalysisTasks: classpath = {0}, capsules = {1}", log_args);
    }
}
