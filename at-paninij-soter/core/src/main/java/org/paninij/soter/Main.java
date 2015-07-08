package org.paninij.soter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Map.Entry;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ipa.callgraph.CGNode;

import edu.illinois.soter.messagedata.MessageInvocation;

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

    
    public static void main(String[] args) throws Exception
    {
        logger.info("Starting up...");
        assertRequiredResourcesExist();
        List<PaniniAnalysis> analyses = parsePaniniAnalyses(CLIArgs.parse(args));

        try
        {
            for (PaniniAnalysis a : analyses) {
                showAnalysisResults(a.perform());
            }
        }
        catch (Throwable ex) {
            logger.severe(ex.toString());
            throw ex;
        }
    }
    
    private static void showAnalysisResults(Map<CGNode, Map<CallSiteReference, MessageInvocation>> messageInvocations)
    {
        for (Entry<CGNode, Map<CallSiteReference, MessageInvocation>> mapEntry : messageInvocations.entrySet()) {
            for (MessageInvocation messageInvocation : mapEntry.getValue().values()) {
                logger.info(messageInvocation.toString());
            }
        }
    }

}
