package org.paninij.soter2;

import java.util.logging.Logger;

import com.ibm.wala.util.WalaException;


/**
 * A PaniniZeroOneCFA that logs information about the analysis's initialized state.
 */
public class NoisyPaniniZeroOneCFA extends PaniniZeroOneCFA
{
    private static final Logger logger = Logger.getLogger(NoisyPaniniZeroOneCFA.class.getName());

    public static PaniniZeroOneCFA make(String name, String classPath) throws WalaException
    {
        PaniniZeroOneCFA analysis = new NoisyPaniniZeroOneCFA(name);
        analysis.init(classPath);
        analysis.perform();
        return analysis;
    }

    public NoisyPaniniZeroOneCFA(String template)
    {
        super(template);
    }

    public void init(String classPath) throws WalaException
    {
        super.init(classPath);
        logger.info("analysisScope: " + analysisScope);
        //logger.info("classHierarchy: " + classHierarchy);  // Too much information...
        logger.info("iClass: " + iClass);
        logger.info("entrypoints: " + entrypoints);
    }
}
