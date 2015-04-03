package org.paninij.apt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.apt.util.DuckShape;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;


class MakeCapsule$Thread extends MakeCapsule$ExecProfile
{
    static MakeCapsule$Thread make(PaniniPress context, TypeElement template)
    {
        MakeCapsule$Thread cap = new MakeCapsule$Thread();
        cap.context = context;
        cap.template = template;
        return cap;
    }


    @Override
    String buildCapsule()
    {
        String pkg = buildPackage();
        String src = Source.lines(0, "package #0;",
                                     "",
                                     "#1",
                                     "",
                                     "/**",
                                     " * This capsule was auto-generated from `#2`",
                                     " */",
                                     "#3",
                                     "{",
                                     "#4",
                                     "}");
        return Source.format(src, pkg,
                                  buildCapsuleImports(),
                                  pkg + "." + template.getSimpleName(),
                                  buildCapsuleDecl(),
                                  buildCapsuleBody());
    }


    @Override
    String buildCapsuleName() {
        return template.getSimpleName() + "$Capsule$Thread";
    }


    @Override
    String buildQualifiedCapsuleName() {
        return template.getQualifiedName() + "$Capsule$Thread";
    }

    @Override
    String buildCapsuleDecl() {
        return "public class " + buildCapsuleName() + " extends Capsule$Thread implements " + template.getSimpleName() + "$Capsule";
    }


    @Override
    String buildCapsuleBody()
    {
        ArrayList<String> decls = new ArrayList<String>();
        decls.add(buildCapsuleFields());
        decls.add("");

        for (Element child : template.getEnclosedElements())
        {
            // TODO: For now, ignore everything except for methods which need to be wrapped
            // procedures. In the future, other enclosed elements may need to be treated specially
            // while building the capsule body.
            if (PaniniModelInfo.needsProcedureWrapper(child)) {
                decls.add(buildProcedure((ExecutableElement) child));
            }
        }

        return String.join("\n", decls);
    }

    @Override
    String buildCapsuleFields()
    {
        String src = Source.lines(0, "#0", "", "#1");
        return Source.format(src, buildPaniniTemplateDecl(), buildProcedureIDs());
    }

    String buildPaniniTemplateDecl() {
        String src = Source.lines(1, "private #0 panini$Template;");
        return Source.format(src, template.getQualifiedName());
    }

    String buildProcedureIDs() {
        ArrayList<String> decls = new ArrayList<String>();
        String src = Source.lines(1, "#0");
        int currID = 0;
        for (Element child : template.getEnclosedElements())
        {
            if (PaniniModelInfo.needsProcedureWrapper(child)) {
                
                decls.add("public static final int " + buildProcedureID((ExecutableElement)child)+ " = " + currID + ";");
                currID++;
            }
        }
        return Source.format(src, String.join("\n    ", decls));
    }
    
    String buildProcedureID(ExecutableElement method)
    {
        String base = "panini$proc$";
        String name = method.getSimpleName().toString();
        List<String> params = new ArrayList<String>();
        for (VariableElement param : method.getParameters()) {
            params.add(parseType(param.asType()));
        }
        String paramStrings = params.size() > 0 ? "$" + String.join("$", params) : "";
        
        return base + name + paramStrings;
    }


    String parseType(TypeMirror type) {
        String src = type.toString().replaceAll("\\.", "_");
        src = src.replaceAll("\\[", "").replaceAll("\\]", "Array");
        return src;
    }

    @Override
    String buildProcedure(ExecutableElement method)
    {
        String src = Source.lines(0, "    #0",
                                     "    {",
                                     "#1",
                                     "    }");

        return Source.format(src, Source.buildExecutableDecl(method),
                                  buildProcedureBody(method));
    }

    String buildProcedureBody(ExecutableElement method) {
        
        DuckShape duck = new DuckShape(method);
        String possibleReturn = "";
       
        // Append the list of parameter names to `args` if there are any.
        String procID = buildProcedureID(method);
        String args = Source.buildParameterNamesList(method);
        args = args.equals("") ? procID : procID + ", " + args;

        if(duck.getSimpleReturnType() != "void")
        {
            possibleReturn = "return panini$duck;";
        }

        String fmt = Source.lines(2, "#0$Thread panini$duck = null;",
                                     "panini$duck = new #0$Thread(#1);",
                                     "panini$push(panini$duck);",
                                     "#2");
        return Source.format(fmt, duck.toString(), args, possibleReturn);
    }
    
    @Override
    Set<String> getStandardImports() {
        Set<String> imports = new HashSet<String>();
        imports.add("org.paninij.runtime.Capsule$Thread");
        imports.add("org.paninij.runtime.ducks.*");
        return imports;
    }
}
