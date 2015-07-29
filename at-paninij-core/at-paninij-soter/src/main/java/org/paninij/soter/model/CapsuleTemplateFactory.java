package org.paninij.soter.model;

import static com.ibm.wala.types.ClassLoaderReference.Application;

import org.paninij.soter.util.WalaUtil;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.TypeReference;

public class CapsuleTemplateFactory
{
    IClassHierarchy cha;

    public CapsuleTemplateFactory(IClassHierarchy cha)
    {
        this.cha = cha;
    }
    
    /**
     * @param capsuleTemplate A fully qualified name of a capsule (e.g. "org.paninij.examples.pi.Pi").
     */
    public CapsuleTemplate make(String capsuleName)
    {
        String templatePath = WalaUtil.fromQualifiedNameToWalaPath(capsuleName) + "Template";

        TypeReference templateReference = TypeReference.find(Application, templatePath);
        if (templateReference == null)
        {
            String msg = "Could not find the `TypeReference` for template: " + templatePath;
            throw new RuntimeException(msg);
        }
        
        IClass templateClass = cha.lookupClass(templateReference);
        if (templateClass == null)
        {
            String msg = "Could not find the `IClass` for template: " + templatePath;
            throw new RuntimeException(msg);
        }

        return new CapsuleTemplate(templateClass);
    }
}
