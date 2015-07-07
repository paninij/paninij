package org.paninij.soter2;

import java.util.Iterator;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.paninij.soter.JavaModel;

import com.ibm.wala.classLoader.IClass;
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
        logger.info("iterateAllApplicationClasses(classHierarchy):\n" + allApplicationClasses());
        logger.info("iClass: " + iClass);
        logger.info("entrypoints: " + entrypoints);
    }
    
    private String allApplicationClasses()
    {
        StringBuilder appClasses = new StringBuilder();
        Iterator<IClass> classIter = JavaModel.iterateAllApplicationClasses(classHierarchy);
        while (classIter.hasNext()) {
            appClasses.append(" " + classIter.next() + "\n");
        }
        return appClasses.toString();
    }
}
