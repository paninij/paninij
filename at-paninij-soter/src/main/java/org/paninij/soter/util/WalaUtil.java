package org.paninij.soter.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.ClassTargetSelector;
import com.ibm.wala.ipa.callgraph.MethodTargetSelector;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.summaries.BypassClassTargetSelector;
import com.ibm.wala.ipa.summaries.BypassMethodTargetSelector;
import com.ibm.wala.ipa.summaries.XMLMethodSummaryReader;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.viz.DotUtil;

public class WalaUtil
{
    public static final String NATIVE_SPEC_RESOURCE_NAME = "natives.xml";
    public static final String DEFAULT_EXCLUSIONS_FILENAME = "Exclusions.txt";

    public static final String DOT_EXECUTABLE = "/usr/local/bin/dot";
    public static final String DOT_TEMPORARY_FILE = "callgraph_temp_file.dot"; 
    
    /**
     * Note that the resulting `AnalysisOptions` object does not have any entrypoints.
     * 
     * @param cha
     * @param scope
     * @return
     */
    public static AnalysisOptions makeAnalysisOptions(IClassHierarchy cha)
    {
        AnalysisScope scope = cha.getScope();
        AnalysisOptions options = new AnalysisOptions(scope, null);

        ClassLoader classLoader = WalaUtil.class.getClassLoader();
        InputStream nativesSpec = classLoader.getResourceAsStream(NATIVE_SPEC_RESOURCE_NAME);
        XMLMethodSummaryReader nativeSpecSummary = new XMLMethodSummaryReader(nativesSpec, scope);

        // Add default selectors then custom ones. The defaults will serve as the parents of the
        // custom children. The children delegate to their parents when they do not definitively
        // target a class/method.
        Util.addDefaultSelectors(options, cha);
        setToSynthesizeNativeMethods(cha, options, nativeSpecSummary);
        setToSynthesizeNativeClasses(cha, options, nativeSpecSummary);

        return options;
    }
    
    
    /**
     * TODO: Is this method name an accurate description of what this bypass logic is doing?
     */
    private static void setToSynthesizeNativeMethods(IClassHierarchy cha,
                                                     AnalysisOptions options,
                                                     XMLMethodSummaryReader natives)
    {
        MethodTargetSelector parent = options.getMethodTargetSelector();
        MethodTargetSelector child = new BypassMethodTargetSelector(parent,
                                                                    natives.getSummaries(),
                                                                    natives.getIgnoredPackages(),
                                                                    cha);
        options.setSelector(child);
    }


    /**
     * TODO: Is this method name an accurate description of what this bypass logic is doing?
     */
    private static void setToSynthesizeNativeClasses(IClassHierarchy cha,
                                                     AnalysisOptions options,
                                                     XMLMethodSummaryReader natives)
    {
        IClassLoader loader = cha.getLoader(cha.getScope().getSyntheticLoader());
        Set<TypeReference> allocatable = natives.getAllocatableClasses();
        ClassTargetSelector parent = options.getClassTargetSelector();

        ClassTargetSelector child = new BypassClassTargetSelector(parent, allocatable, cha, loader);
        options.setSelector(child);
    }


    public static IClassHierarchy makeClassHierarchy(String classPath)
    {
        File exclusions = new File(DEFAULT_EXCLUSIONS_FILENAME);
        AnalysisScope scope;
        IClassHierarchy cha;

        try
        {
            scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(classPath, exclusions);
            cha = ClassHierarchy.make(scope);
            return cha;
        }
        catch (IOException ex)
        {
            String msg = "Failed to read the exclusions file as an analysis scope: "
                       + exclusions.getAbsolutePath();
            throw new IllegalArgumentException(msg);
        }
        catch (ClassHierarchyException ex)
        {
            String msg = "Failed to make class hierarchy.";
            throw new IllegalArgumentException(msg);
        }
    }
    
    
    public static <T> void makeGraphFile(Graph<T> graph, String filename)
    {
        try
        {
            DotUtil.dotify(graph, null, DOT_TEMPORARY_FILE, filename, DOT_EXECUTABLE);
            Path path = FileSystems.getDefault().getPath(DOT_TEMPORARY_FILE);
            Files.delete(path);
        }
        catch (WalaException ex)
        {
            String msg = "Failed to dotify the given graph.";
            throw new IllegalArgumentException(msg);
        }
        catch (IOException ex)
        {
            String msg = "Could not delete the `dot` temporary file.";
            throw new IllegalArgumentException(msg);
        }
    }


    /**
     * @param cha
     * @param templateName The name of the template to be analyzed. Should be something of the form
     *                 `-Lorg/paninij/soter/FooTemplate`.
     */
    public static IClass loadTemplateClass(String templateName, IClassHierarchy cha)
    {
        AnalysisScope scope = cha.getScope();
        ClassLoaderReference appLoaderRef = scope.getApplicationLoader();
		TypeReference typeRef = TypeReference.findOrCreate(appLoaderRef, templateName);

        IClass templateClass = cha.lookupClass(typeRef);
        if (templateClass == null)
        {
            String msg = "Failed to load a template's `IClass`: " + templateName;
            throw new IllegalArgumentException(msg);
        }
        return templateClass;
    }
}
