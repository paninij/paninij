package org.paninij.soter;

import static com.ibm.wala.types.ClassLoaderReference.Application;

import org.paninij.soter.cfa.PaniniCallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.util.WalaUtil;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.TypeReference;

/**
 * A factory for creating and performing `@PaniniJ` SOTER analyses. It caches resources that can be
 * used across multiple SOTER analyses (e.g. the class hierarchy analysis).
 */
public class AnalysisFactory
{
    IClassHierarchy cha;
    AnalysisOptions options;

    public AnalysisFactory(String classPath)
    {
        cha = WalaUtil.makeClassHierarchy(classPath);
        options = WalaUtil.makeAnalysisOptions(cha);
        
        WalaUtil.checkRequiredResourcesExist();
    }
    
    /**
     * @param capsule A fully qualified name of a capsule (e.g. "org.paninij.examples.pi.Pi").
     */
    public Analysis make(String capsuleName)
    {
        String templatePath = WalaUtil.fromQualifiedNameToWalaPath(capsuleName) + "Template";
        IClass templateClass = cha.lookupClass(TypeReference.find(Application, templatePath));
        CapsuleTemplate capsule = new CapsuleTemplate(templateClass);

        PaniniCallGraphAnalysis cfa = new PaniniCallGraphAnalysis();
        cfa.perform(templateClass, cha, options);
        Analysis analysis = new Analysis(capsule, cfa, cha);

        return analysis;
    }
}
