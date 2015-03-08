package org.paninij.apt;

import java.util.ArrayList;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.paninij.apt.util.Source;

public class MakeCapsuleInterface
{
    TypeElement template;
    PaniniPress context;

    static MakeCapsuleInterface make(PaniniPress context, TypeElement template)
    {
        MakeCapsuleInterface sig = new MakeCapsuleInterface();
        sig.context = context;
        sig.template = template;
        return sig;
    }

    void makeSourceFile()
    {
        context.createJavaFile(buildQualifiedCapsuleName(), buildCapsuleInterface());
    }

    private String buildCapsuleInterface()
    {
        String pkg = buildPackage();
        String src = Source.lines(0, "package #0;",
                "",
                "#1",
                "",
                "/**",
                " * This Capsule Interface was auto-generated from `#2`",
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

    private String buildCapsuleDecl()
    {
        return "public interface " + buildCapsuleName();
    }

    private String buildCapsuleName()
    {
        return template.getSimpleName() + "$Capsule";
    }

    private String buildCapsuleImports()
    {
        // TODO Auto-generated method stub
        return "";
    }

    private String buildPackage()
    {
        return context.getPackageOf(template);
    }

    private String buildQualifiedCapsuleName()
    {
        return template.getQualifiedName() + "$Capsule";
    }

    private String buildCapsuleBody()
    {
        ArrayList<String> decls = new ArrayList<String>();
        for (Element child : template.getEnclosedElements())
        {
            if (child.getKind() == ElementKind.METHOD)
            {
                decls.add(buildMethodSignature((ExecutableElement) child));
            }
        }
        return String.join("\n", decls);
    }

    private String buildMethodSignature(ExecutableElement method)
    {
        String parameters = buildMethodParameters(method);
        // TODO include "throws"
        return Source.format("public #0 #1(#2);", method.getReturnType(), method.getSimpleName(), parameters);
    }

    private String buildMethodParameters(ExecutableElement method)
    {
        String parameters = "";
        for (VariableElement param : method.getParameters())
        {
            parameters += param.asType() + " " + param.getSimpleName() + ", ";
        }
        return parameters.replaceAll(", $", "");
    }
}
