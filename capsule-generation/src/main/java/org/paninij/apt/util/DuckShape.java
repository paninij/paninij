package org.paninij.apt.util;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.paninij.apt.PaniniPress;

public class DuckShape
{

    public final TypeElement returnType;
    public final List<String> parameters;
    public final DuckShape.Category category;

    public static DuckShape make(PaniniPress context, ExecutableElement method)
    {
        TypeElement rtElement;
        TypeMirror rtMirror = method.getReturnType();
        if (rtMirror.getKind() == TypeKind.VOID)
        {
            rtElement = null;
        }
        else
        {
            rtElement = (TypeElement) context.getTypeUtils().asElement(method.getReturnType());
        }

        List<String> params = new ArrayList<String>();

        for (VariableElement param : method.getParameters())
        {
            params.add(param.asType().toString());
        }

        return new DuckShape(rtElement, params);
    }

    // TODO: Override Object.hashCode() to utilize the parameter/returntype
    // string as the hashCode

    public DuckShape(TypeElement returnType, List<String> parameters)
    {
        this.returnType = returnType;
        this.parameters = parameters;
        // TODO: Infer category from the returnType object
        this.category = defineCategory(returnType);
    }

    private DuckShape.Category defineCategory(TypeElement returnType)
    {
        // TODO: Add checks for all categories
        if (returnType == null)
        {
            return Category.VOID;
        }
        else
        {
            return Category.NORMAL;
        }

    }
    
    public String toString()
    {
        String ret = "";
        if(this.returnType == null)
        {
            ret += "void$Duck";
        }
        else
        {
            ret += Source.dropPackageName(this.returnType.asType().toString());
            ret += "$Duck";
        }
        
        if(this.parameters.size() == 0)
        {
            ret += "$void";
        }
        else
        {
            for(String param : this.parameters)
            {
                ret += "$" + Source.dropPackageName(param);
            }
        }
        
        return ret;
           
        
        
    }

    public boolean equals(DuckShape otherDuck)
    {

        if (!this.returnType.equals(otherDuck.returnType))
        {
            return false;
        }
        if (this.parameters.size() != otherDuck.parameters.size())
        {
            return false;
        }
        for (int index = 0; index < this.parameters.size(); index++)
        {
            if (!this.parameters.get(index).equals(otherDuck.parameters.get(index)))
            {
                return false;
            }
        }
        return true;
    }
    
    public String getSimpleReturnType()
    {
        return Source.dropPackageName(this.returnType.asType().toString());
    }
    
    public String getQualifiedReturnType()
    {
        return this.returnType.asType().toString();
    }
    
    

    public enum Category
    {
        NORMAL, VOID, FINAL, ARRAY, FINALARRAY, PRIMITIVE, PANINICUSTOM
    }

}
