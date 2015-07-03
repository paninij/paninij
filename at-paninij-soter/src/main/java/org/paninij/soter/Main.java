package org.paninij.soter;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;
import edu.illinois.soter.messagedata.MessageArgument;
import edu.illinois.soter.messagedata.MessageInvocation;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * 
 * @author David Johnston
 *
 */
public class Main
{
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final String[] REQUIRED_RESOURCES = {
        "wala.properties",
        "Exclusions.txt",
        "primordial.txt",
        "primordial.jar.model",
        "natives.xml"
    };
    
    private static class CLIArgs
    {
        @Parameter(names = "-classpath", description = "Classpath")
        public String classpath;
        
        @Parameter(description = "Capsules")
        public final List<String> capsules = new ArrayList<String>();
        
        public String toString()
        {
            String fmt = "CLIArgs: classpath = {0},\n"
                       + "         capsules = {1}";
            return MessageFormat.format(fmt, classpath, capsules);
        }
        
        public static CLIArgs parse(String[] args)
        {
            CLIArgs cli_args = new CLIArgs();
            new JCommander(cli_args, args);
            return cli_args;
        }
    }
    
    
    public static List<PaniniAnalysis> parsePaniniAnalyses(CLIArgs cli_args)
    {
        return cli_args.capsules.stream().map(c -> new PaniniAnalysis(c, cli_args.classpath))
                                         .collect(toList());
    }
    
    
    public static void assertRequiredResourcesExist()
    {
        for (String res : REQUIRED_RESOURCES)
        {
            if (Main.class.getClassLoader().getResource(res) == null)
            {
                String msg = "A required resource does not exist.";
                RuntimeException ex = new MissingResourceException(msg, "Main", res);
                logger.severe(ex.toString());
                throw ex;
            }
        }
    }

    
    public static void main(String[] args)
    {
        logger.info("Starting up...");
        assertRequiredResourcesExist();
        List<PaniniAnalysis> analyses = parsePaniniAnalyses(CLIArgs.parse(args));

        try
        {
            for (PaniniAnalysis a : analyses) {
                a.perform();
            }
            List<String> resultsList = analyses.stream().map(a -> a.getResultString()).collect(toList());
            for (String results : resultsList) {
                logger.info(results);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
