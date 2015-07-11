package org.paninij.soter2;

import java.util.Iterator;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.paninij.soter.util.JavaModel;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.util.WalaException;


/**
 * A PaniniZeroOneCFA that logs information about the analysis's initialized state.
 */
public class NoisyPaniniCallGraphBuilder extends PaniniCallGraphBuilder
{
    private static final Logger logger = Logger.getLogger(NoisyPaniniCallGraphBuilder.class.getName());

    public static PaniniCallGraphBuilder make(String name, String classPath) throws WalaException
    {
        PaniniCallGraphBuilder analysis = new NoisyPaniniCallGraphBuilder(name);
        analysis.init(classPath);
        analysis.perform();
        return analysis;
    }

    public NoisyPaniniCallGraphBuilder(String template)
    {
        super(template);
    }

    public void init(String classPath) throws WalaException
    {
        super.init(classPath);

        logger.info("analysisScope: " + analysisScope);
        logger.info("allApplicationClasses(classHierarchy):\n"
                      + JavaModel.getApplicationClassesString(classHierarchy));
        logger.info("iClass: " + templateClass);
        logger.info("entrypoints: " + entrypoints);
    }
}
