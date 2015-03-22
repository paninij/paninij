package org.paninij.apt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.apt.util.DuckShape;
import org.paninij.apt.util.Source;


class MakeCapsule$Thread extends MakeCapsule
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
    String buildCapsuleImports() {
        return "import org.paninij.runtime.Capsule$Thread;\nimport org.paninij.runtime.ducks.*;";
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
            // For now, ignore everything except for constructors and methods which need to be
            // wrapped with procedures.
            if (child.getKind() == ElementKind.CONSTRUCTOR) {
                // TODO:
                //decls.add(buildCapsuleFactory((ExecutableElement) child));
            } else if (needsProceedureWrapper(child)) {
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
            if (needsProceedureWrapper(child)) {
                
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

        String src = Source.lines(1, "public #0 #1(#2)",
                                     "{",
                                     "#3",
                                     "}");

        return Source.format(src, method.getReturnType(),
                                        method.getSimpleName(),
                                        buildProcedureParameters(method),
                                        buildProcedureBody(method));
    }

    String buildProcedureBody(ExecutableElement method) {
        
        DuckShape duck = new DuckShape(method);
        String possibleReturn = "";
        String fmt = Source.lines(0, "    #0$Thread panini$duck$future = null;",
                                     "        panini$duck$future = new #0$Thread(#1);",
                                     "        panini$push(panini$duck$future);",
                                     "        #2");
        
        List<String> args = new ArrayList<String>();
        
        args.add(buildProcedureID(method));
        
        for(Element el : method.getParameters())
        {
            VariableElement param = (VariableElement)el;
            args.add(param.toString());
        }
        
        if(duck.getSimpleReturnType() != "void")
        {
            possibleReturn = "return panini$duck$future;";
        }
        
        return Source.format(fmt, duck.toString(), String.join(", ", args), possibleReturn);
    }


    @Override
    String buildProcedureParameters(ExecutableElement method)
    {
        //TODO: Use version in Source
        List<String> paramStrings = new ArrayList<String>();
        for (VariableElement param : method.getParameters()) {
            paramStrings.add(buildParamDecl(param));
        }
        return String.join(", ", paramStrings);
    }


    @Override
    String buildArgsList(ExecutableElement method)
    {
        //TODO: Use version in Source
        List<String> paramStrings = new ArrayList<String>();
        for (VariableElement var : method.getParameters()) {
            paramStrings.add(var.toString());
        }
        return String.join(", ", paramStrings);
    }


    @Override
    String buildParamDecl(VariableElement param)
    {
        return param.asType().toString() + " " + param.toString();
    }

    boolean needsProceedureWrapper(Element elem)
    {
        if (elem.getKind() == ElementKind.METHOD) {
            ExecutableElement method = (ExecutableElement) elem;
            Set<Modifier> modifiers = method.getModifiers();
            // TODO: decide on appropriate semantics.
            if (modifiers.contains(Modifier.STATIC)) {
            	return false;
            } else if (modifiers.contains(Modifier.PUBLIC)) {
                return true;
            }
        }

        return false;
    }
}
