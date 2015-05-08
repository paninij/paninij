package org.paninij.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;

import org.paninij.apt.util.DuckShape;
import org.paninij.apt.util.JavaModelInfo;
import org.paninij.apt.util.Source;

public abstract class MakeDuck
{
    PaniniProcessor context;

    public static MakeDuck make(PaniniProcessor context)
    {
        throw new UnsupportedOperationException("Cannot instantiate an abstract class.");
    }

    String buildDuck(DuckShape currentDuck)
    {
        DuckShape.Category category = currentDuck.category;
        switch (category)
        {
        case NORMAL:
            return buildNormalDuck(currentDuck);
        case VOID:
            return buildVoidDuck(currentDuck);
        case PANINICUSTOM:
            return buildPaniniCustomDuck(currentDuck);
        default:
            throw new UnsupportedOperationException("Duck category not supported: " + category);
        }
    }
    
    // TODO: Consider replacing uses of `buildPackage()` with calls to `currentDuck.getPackage()`.
    String buildPackage(DuckShape currentDuck)
    {
        return currentDuck.getPackage();
    }

    abstract String buildClassName(DuckShape currentDuck);

    abstract String buildQualifiedClassName(DuckShape currentDuck);

    String buildParameterFields(DuckShape currentDuck)
    {
        String fieldsStr = "";
        for (int i = 0; i < currentDuck.slotTypes.size(); i++)
        {
            fieldsStr += "    public " + currentDuck.slotTypes.get(i) + " panini$arg" + i + ";\n";
        }
        return fieldsStr;
    }

    String buildFacades(DuckShape currentDuck)
    {
        String facades = "";
        
        DeclaredType returnType = (DeclaredType) currentDuck.returnType;

        for (Element el : returnType.asElement().getEnclosedElements())
        {
            if (el.getKind() == ElementKind.METHOD)
            {
                ExecutableElement method = (ExecutableElement) el;
                if (this.canMakeFacade(method))
                {
                    facades += buildFacade(method);
                }

            }
        }

        return facades;
    }

    String buildFacade(ExecutableElement method)
    {
        String fmt = Source.lines(1, 
                "", 
                "@Override", 
                "#0 {", 
                "    #1", 
                "}");
        return Source.format(fmt, Source.buildExecutableDecl(method), buildFacadeBody(method));

    }

    String buildFacadeBody(ExecutableElement method)
    {
        String fmt;
        if (JavaModelInfo.hasVoidReturnType(method))
        {
            fmt = "panini$get().#0(#1);";
        }
        else
        {
            fmt = "return panini$get().#0(#1);";
        }
        return Source.format(fmt, method.getSimpleName(), Source.buildParameterNamesList(method));
    }

    boolean canMakeFacade(ExecutableElement method)
    {
        // Some methods do not need to have a facade made for them
        // e.g. native methods, final methods
        String modifiers = Source.buildModifiersList(method);
        if (modifiers.contains("native"))
        {
            return false;
        }
        if (modifiers.contains("final"))
        {
            return false;
        }
        if (modifiers.contains("protected"))
        {
            return false;
        }
        if (modifiers.contains("private"))
        {
            return false;
        }
        if (modifiers.contains("private"))
        {
            return false;
        }
        if (modifiers.contains("static"))
        {
            return false;
        }
        return true;
    }
    
    String buildReleaseArgs(DuckShape currentDuck) {
        
        String args = "";
        for(int i = 0; i < currentDuck.slotTypes.size(); i++)
        {
            if(currentDuck.slotTypes.get(i).equals("Object"))
            {
                args += "        panini$arg" + i + " = null;\n";
            }
        }
            
        return args;
    }

    abstract String buildConstructor(DuckShape currentDuck);

    abstract String buildConstructorDecl(DuckShape currentDuck);

    abstract String buildNormalDuck(DuckShape currentDuck);

    abstract String buildVoidDuck(DuckShape currentDuck);

    abstract String buildPaniniCustomDuck(DuckShape currentDuck);

    public abstract void makeSourceFile(DuckShape currentDuck);
}
