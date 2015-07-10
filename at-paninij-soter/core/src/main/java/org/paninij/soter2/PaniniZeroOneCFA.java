package org.paninij.soter2;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.ibm.wala.analysis.pointers.BasicHeapGraph;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PropagationCallGraphBuilder;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.config.AnalysisScopeReader;


public class PaniniZeroOneCFA extends ZeroOneCFA<InstanceKey>
{
    public static final String DEFAULT_EXCLUSIONS_FILENAME = "Exclusions.txt";
    
    protected String templateName;
    protected AnalysisScope analysisScope;
    protected ClassHierarchy classHierarchy;
    protected IClass templateClass;
    protected Set<Entrypoint> entrypoints;
    

    public static PaniniZeroOneCFA make(String name, String classPath) throws WalaException
    {
        PaniniZeroOneCFA analysis = new PaniniZeroOneCFA(name);
        analysis.init(classPath);
        analysis.perform();
        return analysis;
    }
    

    /**
     * @param templateName The name of the template to be analyzed. Should be something of the form
     *                 `-Lorg/paninij/soter/FooTemplate`.
     */
    public PaniniZeroOneCFA(String templateName) {
        this.templateName = templateName;
    }
 

    public void init(String classPath) throws WalaException
    {
        initAnalysisScope(classPath, DEFAULT_EXCLUSIONS_FILENAME);
        initClassHierarchy();
        initTemplateClass();
        initEntrypoints();
    }
    

    protected void initAnalysisScope(String classPath, String exclusionsFilename)
    {
        try
        {
            File exclusions = new File(exclusionsFilename);
            analysisScope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(classPath, exclusions);
        }
        catch (IOException e)
        {
            String msg = "Failed to read the exclusions file as an analysis scope: "
                       + exclusionsFilename;
            throw new IllegalArgumentException(msg);
        }
    }


    protected void initClassHierarchy() throws WalaException
    {
        this.classHierarchy = ClassHierarchy.make(analysisScope);
    }
    

    protected void initTemplateClass()
    {
        ClassLoaderReference appLoaderRef = analysisScope.getApplicationLoader();
		TypeReference typeRef = TypeReference.findOrCreate(appLoaderRef, templateName);
        this.templateClass = classHierarchy.lookupClass(typeRef);
        if (this.templateClass == null)
        {
            String msg = "Initialization of a template's `IClass` failed: " + templateName;
            throw new IllegalArgumentException(msg);
        }
    }
    

    protected void initEntrypoints()
    {
        entrypoints = CapsuleTemplateEntrypoint.makeAll(templateClass);
    }
    
    
    @Override
    @SuppressWarnings("unchecked")
    public void perform()
    {
        AnalysisOptions options = new AnalysisOptions(analysisScope, entrypoints);
        Util.addDefaultSelectors(options, classHierarchy);

        PropagationCallGraphBuilder builder = Util.makeZeroOneCFABuilder(options,
                                                                         new AnalysisCache(),
                                                                         classHierarchy,
                                                                         analysisScope);
        try
        {
            callGraph = builder.makeCallGraph(options, null);
            pointerAnalysis = builder.getPointerAnalysis();
            heapModel = pointerAnalysis.getHeapModel();
            heapGraph = new BasicHeapGraph(pointerAnalysis, callGraph);
        }
        catch (CallGraphBuilderCancelException ex)
        {
            String msg = "Call graph construction was unexpectedly cancelled: " + templateName;
            throw new IllegalArgumentException(msg);
        }
    }

}
