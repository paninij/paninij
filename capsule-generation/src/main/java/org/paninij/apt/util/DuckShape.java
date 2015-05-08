package org.paninij.apt.util;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;


/**
 * Note that `returnType == null` whenever the duck describes a method with no return type.
 * Note that `slotTypes.size() == 0` whenever the duck describes a method which takes no arguments.
 */
public class DuckShape
{
    public final Category category;
    public final TypeMirror returnType;
    public final List<String> slotTypes;
    public final String encoded;


    public DuckShape(ExecutableElement method)
    {
        assert (method != null);
        
        category = categoryOf(method);
        returnType = (category == Category.VOID) ? null : method.getReturnType();
        slotTypes = getSlotTypes(method);
        encoded = encode(method);
    }
    
    
    public enum Category
    {
        NORMAL,
        VOID,
        FINAL,
        ARRAY,
        FINALARRAY,
        PRIMITIVE,
        PANINICUSTOM
    }
    

    public static DuckShape.Category categoryOf(ExecutableElement method)
    {
        // TODO: Add checks for all categories.
        
        TypeMirror returnType = method.getReturnType();
        
        if (JavaModelInfo.hasVoidReturnType(method))
        {
            return Category.VOID;
        }
        else if (JavaModelInfo.isFinalType(returnType))
        {
            return Category.FINAL;
        }
        else if (JavaModelInfo.isArray(returnType))
        {
            return Category.ARRAY;
        }
        else if (JavaModelInfo.isPrimitive(returnType))
        {
            return Category.PRIMITIVE;
        }
        else if (PaniniModelInfo.isPaniniCustom(returnType))
        {
            return Category.PANINICUSTOM;
        }
        else
        {
            return Category.NORMAL;
        }

        /*
        String msg = "Can't identify the `DuckShape.Category` of the given `method`: " + method;
        throw new IllegalArgumentException(msg);
        */
    }
 
    
    private static List<String> getSlotTypes(ExecutableElement method)
    {
        List<String> slotTypes = new ArrayList<String>();
        for (VariableElement param : method.getParameters())
        {
            slotTypes.add(getSlotType(param));
        }
        return slotTypes;
    }
    
    
    private static String getSlotType(VariableElement param)
    {
        // TODO: Look for another way to get the strings in the primitive cases.
        TypeKind kind = param.asType().getKind();

        switch (kind) {

        case ARRAY:
        case DECLARED:
            return "Object";

        case BOOLEAN:
            return "boolean";
        case BYTE:
            return "byte";
        case CHAR:
            return "char";
        case DOUBLE:
            return "double";
        case FLOAT:
            return "float";
        case INT:
            return "int";
        case LONG:
            return "long";
        case SHORT:
            return "short";

        default:
            // TODO: remove trailing space character after GitHub issue #24 is resolved.
            String msg = "The given `param` (of the form `#0`) has an unexpected `TypeKind`: #1 ";
            msg = Source.format(msg, Source.buildVariableDecl(param), kind.toString());
            throw new IllegalArgumentException(msg);
        }
    }


    public static String encode(ExecutableElement method)
    {
        return encodeReturnType(method) + "$Duck$" + encodeSlots(method);
    }

 
    private static String encodeSlots(ExecutableElement method)
    {
        List<String> slotTypes = getSlotTypes(method);
        return encodeSlots(slotTypes);
    }
    
    
    private static String encodeSlots(List<String> slotTypes)
    {
        return String.join("$", slotTypes);
    }
 
  
    private static String encodeReturnType(ExecutableElement method)
    {
        // TODO: Handle other cases.
        Category category = categoryOf(method);
        switch(category) {

        case VOID:
            return "void";

        case NORMAL:
        case PANINICUSTOM:
            return method.getReturnType()
                         .toString()
                         .replaceAll("_", "__")
                         .replaceAll("\\.", "_");

        case FINAL:
        case FINALARRAY:
        case PRIMITIVE:
        default:
            throw new IllegalArgumentException("Cannot handle duck category: " + category);
        }
    }


    /**
     * Returns the name of the return type associated with the duck shape. If there is no return
     * type (i.e. `returnType == null`) then "void" is returned.
     */
    public String getSimpleReturnType()
    {
        if (returnType == null) {
            return "void";
        } else {
            return Source.dropPackageName(returnType.toString());
        }
    }


    /**
     * Returns the fully qualified name of the return type associated with the duck shape. If there
     * is no return type (i.e. `returnType == null`) then "void" is returned.
     */
    public String getQualifiedReturnType()
    {
        if (returnType == null) {
            return "void";
        } else {
            return returnType.toString();
        }
    }
    
    
    /**
     * Returns the string representation of the package in which the duck should be put. This is
     * generally based on the `returnType` of the duck itself.
     * 
     * If the duck has no return type (i.e. its category is `VOID`).
     */
    public String getPackage()
    {
        switch (category) {
        case VOID:
            return PaniniModelInfo.DEFAULT_DUCK_PACKAGE;
        default:
            return JavaModelInfo.getPackage(returnType);
        }
    }


    public String toString()
    {
        return encoded;
    }
    
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof DuckShape))
            return false;
        
        DuckShape other = (DuckShape) o;
        return this.encoded.equals(other.encoded);
    }
    
    
    @Override
    public int hashCode()
    {
        return encoded.hashCode();
    }
}
