package org.paninij.soter.model;

import org.paninij.runtime.util.IdentitySet;
import org.paninij.soter.util.PaniniModel;
import org.paninij.soter.util.WalaUtil;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.types.TypeName;

public class CapsuleTemplate
{
    protected TypeName typeName;
    protected String qualifiedName;

    // Panini Model Elements:
    protected IClass template;
    protected IMethod initDecl;
    protected IMethod designDecl;
    protected IMethod runDecl;
    protected IdentitySet<IMethod> procedures = new IdentitySet<IMethod>();
    protected IdentitySet<IMethod> methods = new IdentitySet<IMethod>();
    
    /**
     * @param template A capsule template class.
     */
    public CapsuleTemplate(IClass template)
    {
        if (PaniniModel.isCapsuleTemplate(template) == false) {
            String msg = "The `template` argument is not a capsule template: " + template;
            throw new IllegalArgumentException(msg);
        }

        typeName = template.getName();
        qualifiedName = toQualifiedName(typeName);
        
        initModelElements(template);
    }
    
    private void initModelElements(IClass templateClass)
    {
        template = templateClass;
        for (IMethod method : template.getDeclaredMethods())
        {
            switch (method.getName().toString())
            {
            case "init":
                initDecl = method;
                continue;
            case "design":
                designDecl = method;
                continue;
            case "run":
                runDecl = method;
                continue;
            default:
                if (method.isClinit() || method.isInit() || method.isStatic() || method.isNative())
                    continue;
                if (PaniniModel.isProcedure(method)) {
                    procedures.add(method);
                } else {
                    methods.add(method);
                }
            }
        }        
    }

    public IClass getTemplateClass()
    {
        return template;
    }
    
    public String getWalaPath()
    {
        return WalaUtil.fromQualifiedNameToWalaPath(qualifiedName);
    }

    /**
     * @param qualifiedName A fully-qualified capsule name of the form "org/paninij/examples/pi/Pi".
     */
    public String getQualifiedName()
    {
        return qualifiedName;
    }
    
    private static String toQualifiedName(TypeName typeName)
    {
        String packageName = new String(typeName.getPackage().getValArray());
        String className = new String(typeName.getClassName().getValArray());
        if (packageName.isEmpty()) {
            return className;
        } else {
            return packageName + "/" + className;
        }
    }
}
