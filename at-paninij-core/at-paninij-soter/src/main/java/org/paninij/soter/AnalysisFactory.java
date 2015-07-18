package org.paninij.soter;

import org.paninij.soter.model.Analysis;
import org.paninij.soter.model.Capsule;
import org.paninij.soter.util.WalaUtil;

import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.cha.IClassHierarchy;

/**
 * A factory for creating and performing `@PaniniJ` SOTER analyses. It caches resources that can be
 * used across multiple SOTER analyses (e.g. the class hierarchy analysis).
 */
public class AnalysisFactory
{
    CallGraphBuilder builder;
    IClassHierarchy cha;
    AnalysisOptions options;

    public AnalysisFactory(String classPath)
    {
        cha = WalaUtil.makeClassHierarchy(classPath);
        options = WalaUtil.makeAnalysisOptions(cha);
        builder = new CallGraphBuilder();
        
        WalaUtil.checkRequiredResourcesExist();
    }
    
    public Analysis analyze(Capsule capsule)
    {
        String templatePath = capsule.getWalaPath() + "Template";
        builder.buildCallGraph(templatePath, cha, options);
        Analysis analysis = new Analysis(capsule, builder.getCallGraph(), builder.getHeapGraph(),
                                         builder.getPointerAnalysis(), cha);
        return analysis;
    }
}
