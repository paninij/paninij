package org.paninij.apt;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;

public class MakeCapsule
{
    TypeElement template;
    PaniniPress context;

    static MakeCapsule make(PaniniPress context, TypeElement template)
    {
        MakeCapsule sig = new MakeCapsule();
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
                                  buildImports(),
                                  pkg + "." + template.getSimpleName(),
                                  buildCapsuleDecl(),
                                  buildCapsuleBody());
    }

    private String buildCapsuleDecl()
    {
        return "public interface " + buildCapsuleName() + buildCapsuleInterfaces();
    }

    private String buildCapsuleInterfaces()
    {
        List<? extends TypeMirror> interfaces = template.getInterfaces();
        if (interfaces.size() > 0)
        {
            String extend = " extends ";
            for (TypeMirror i : interfaces)
            {
                Element interf = ((DeclaredType) i).asElement();
                extend += interf.getSimpleName() + "$Signature, ";
                // TODO: Verify that it is indeed a signature?
                // Or maybe verification is part of the Checker class.
            }
            return extend.replaceAll(", $", "");
        }
        else
        {
            // There are no signatures to extend.
            return "";
        }
    }

    private String buildCapsuleName()
    {
        return template.getSimpleName() + "$Capsule";
    }

    private String buildImports()
    {
        return Source.buildCollectedImportDecls(template);
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
            if (PaniniModelInfo.needsProcedureWrapper(child))
            {
                ExecutableElement method = (ExecutableElement) child;
                String decl = Source.format("    #0;\n", Source.buildExecutableDecl(method));
                decls.add(decl);
            }
        }
        return String.join("", decls);
    }
}
