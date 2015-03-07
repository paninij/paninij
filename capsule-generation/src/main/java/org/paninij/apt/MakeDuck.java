package org.paninij.apt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.TypeElement;

import org.paninij.apt.util.DuckShape;
import org.paninij.apt.util.Source;

public abstract class MakeDuck
{

    final String packageName = "org.paninij.runtime.ducks";
    PaniniPress context;
    
    public static MakeDuck make(PaniniPress context)
    {
        throw new UnsupportedOperationException("Cannot instantiate an abstract class.");
    }
    
    String buildDuck(DuckShape currentDuck)
    {
        switch(currentDuck.category)
        {
        case NORMAL:
            return buildNormalDuck(currentDuck);
        case VOID:
            return buildVoidDuck(currentDuck);
        default:
            throw new UnsupportedOperationException("Duck category not supported.");
        }
    }
    
    abstract String buildClassName(DuckShape currentDuck);
    abstract String buildQualifiedClassName(DuckShape currentDuck);
    
    String buildParameterImports(DuckShape currentDuck)
    {
        String importsStr = "";
        Set<String> uniqueParams = new HashSet<String>(currentDuck.parameters);
        for(String param : uniqueParams)
        {
            importsStr += "import " + param + ";\n";
        }
        return importsStr;
    }
    
    String buildParameterFields(DuckShape currentDuck)
    {
        String fieldsStr = "";
        for(int i = 0; i < currentDuck.parameters.size(); i++)
        {
            fieldsStr += "    public final " + Source.dropPackageName(currentDuck.parameters.get(i)) + " panini$arg" + i + ";\n";
        }
        return fieldsStr;
    }
    
    abstract String buildConstructor(DuckShape currentDuck);
    abstract String buildConstructorDecl(DuckShape currentDuck);
    abstract String buildNormalDuck(DuckShape currentDuck);

    abstract String buildVoidDuck(DuckShape currentDuck);

    public abstract void makeSourceFile(DuckShape currentDuck);
}
