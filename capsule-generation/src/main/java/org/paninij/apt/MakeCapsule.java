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

/**
 * This class contains logic to inspect a given capsule template class and generate the capsule
 * interface which every concrete capsule type (e.g. `Foo$Thread`, `Bar$Task`) will implement.
 *
 * For example, if the given capsule template is named `Baz$Template`, then this class will make a
 * `Baz` interface. For each procedure defined in `Baz$Template`, an equivalent declarations will
 * be added to the `Baz` interface.
 */
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
        String src = Source.lines(0, "package #0;",
                                     "",
                                     "#1",
                                     "",
                                     "/**",
                                     " * This capsule interface was auto-generated from `#2`",
                                     " */",
                                     "@CapsuleInterface",
                                     "#3",
                                     "{",
                                     "#4",
                                     "}");

        return Source.format(src, buildPackage(),
                                  buildImports(),
                                  PaniniModelInfo.qualifiedTemplateName(template),
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
                extend += interf.getSimpleName() + ", ";
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
        return PaniniModelInfo.simpleCapsuleName(template);
    }

    private String buildQualifiedCapsuleName()
    {
        return PaniniModelInfo.qualifiedCapsuleName(template);
    }

    private String buildImports()
    {
        return Source.buildCollectedImportDecls(template, "org.paninij.lang.CapsuleInterface");
    }

    private String buildPackage()
    {
        return context.getPackageOf(template);
    }

    private String buildCapsuleBody()
    {
        ArrayList<String> decls = new ArrayList<String>();

        String designDecl = PaniniModelInfo.buildCapsuleDesignMethodDecl(template);
        if (designDecl != null) {
            decls.add("    " + designDecl + ";\n");
        }

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
