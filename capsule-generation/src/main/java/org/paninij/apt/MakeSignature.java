package org.paninij.apt;

import java.util.ArrayList;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import org.paninij.apt.util.Source;


public class MakeSignature
{
    TypeElement template;
    PaniniPress context;

    static MakeSignature make(PaniniPress context, TypeElement template)
    {
        MakeSignature sig = new MakeSignature();
        sig.context = context;
        sig.template = template;
        return sig;
    }

    void makeSourceFile()
    {
        context.createJavaFile(buildQualifiedSignatureName(), buildSignature());
    }

    String buildSignature()
    {
        String pkg = buildPackage();
        String src = Source.lines(0, "package #0;",
                "",
                "#1",
                "",
                "/**",
                " * This signature was auto-generated from `#2`",
                " */",
                "#3",
                "{",
                "#4",
                "}");
        return Source.format(src, pkg,
                buildSignatureImports(),
                pkg + "." + template.getSimpleName(),
                buildSignatureDecl(),
                buildSignatureBody());
    }

    String buildPackage()
    {
        return context.getPackageOf(template);
    }

    String buildSignatureBody()
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

    String buildSignatureImports()
    {
        // TODO
        return "";
    }

    String buildSignatureName()
    {
        return template.getSimpleName() + "$Signature";
    }

    String buildQualifiedSignatureName()
    {
        return template.getQualifiedName() + "$Signature";
    }

    String buildSignatureDecl() {
        return "public interface " + buildSignatureName();
    }

    String buildMethodSignature(ExecutableElement method)
    {
        String parameters = buildMethodParameters(method);
        // TODO include "throws"
        return Source.format("public #0 #1(#2);", method.getReturnType(), method.getSimpleName(), parameters);
    }

    String buildMethodParameters(ExecutableElement method)
    {
        String parameters = "";
        for (VariableElement param : method.getParameters())
        {
            parameters += param.asType() + " " + param.getSimpleName() + ", ";
        }
        return parameters.replaceAll(", $", "");
    }
}
