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
        IClass templateClass = cha.lookupClass(TypeReference.find(Application, templatePath));
        return new CapsuleTemplate(templateClass);
    }
}
