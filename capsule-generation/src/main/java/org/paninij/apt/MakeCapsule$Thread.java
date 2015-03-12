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
                                     "#5",
                                     "#6",
                                     "#7",
                                     "#8",
                                     "}");
        return Source.format(src, pkg,
                                  buildCapsuleImports(),
                                  pkg + "." + template.getSimpleName(),
                                  buildCapsuleDecl(),
                                  buildPaniniStart(),
                                  buildPaniniExit(),
                                  buildPaniniJoin(),
                                  buildPaniniShutdown(),
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
        return "import org.paninij.runtime.Capsule$Thread;";
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
        /*
    	String src = Source.lines(1,
            "private LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();",
            "private {0} encapsulated = new {0}();",
            "private Thread thread;"
        );
        return MessageFormat.format(src, template.getSimpleName());
        */
        return "";
    }


    @Override
    String buildProcedure(ExecutableElement method)
    {

        String src = Source.lines(1, "public #0 #1(#2)",
                                     "{",
                                     "    #3",
                                     "}");

        return Source.format(src, method.getReturnType(),
                                        method.getSimpleName(),
                                        buildProcedureParameters(method),
                                        buildProcedureBody(method));
    }

    String buildProcedureBody(ExecutableElement method) {
        return "throw new UnsupportedOperationException();";
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

    String buildPaniniExit()
    {
        String src = Source.lines(1, "@Override",
                "public void panini$exit()",
                "{",
                "    //TODO",
                "}");
        return Source.format(src);
    }

    String buildPaniniJoin()
    {
        String src = Source.lines(1, "@Override",
                "public void panini$join()",
                "{",
                "    //TODO",
                "}");
        return Source.format(src);
    }

    String buildPaniniStart()
    {
        String src = Source.lines(1, "@Override",
                "public void panini$start()",
                "{",
                "    //TODO",
                "}");
        return Source.format(src);
    }

    String buildPaniniShutdown()
    {
        String src = Source.lines(1, "@Override",
                "public void panini$shutdown()",
                "{",
                "    //TODO",
                "}");
        return Source.format(src);
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
