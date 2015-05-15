/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills, David Johnston, Trey Erenberger
 */
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
    PaniniProcessor context;

    static MakeCapsule make(PaniniProcessor context, TypeElement template)
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
        String src = Source.cat("package #0;",
                                "",
                                "##",
                                "",
                                "/**",
                                " * This capsule interface was auto-generated from `#1`",
                                " */",
                                "@CapsuleInterface",
                                "#2",
                                "{",
                                "    #3",
                                "    ##",
                                "}");

        src = Source.format(src, buildPackage(),
                                 PaniniModelInfo.qualifiedTemplateName(template),
                                 buildCapsuleDecl(),
                                 buildWireMethodDecl());
        src = Source.formatAligned(src, buildImports());
        src = Source.formatAligned(src, buildExecutableDecls());

        return src;
    }


    private String buildCapsuleDecl()
    {
        return Source.format("public interface #0 extends #1", buildCapsuleName(),
                                                               buildCapsuleInterfaces());
    }


    private String buildCapsuleInterfaces()
    {
        List<String> interfaces = new ArrayList<String>();
        interfaces.add("Panini$Capsule");
        for (TypeMirror i : template.getInterfaces()) {
            interfaces.add(i.toString());
        }
        return String.join(", ", interfaces);
    }


    private String buildCapsuleName()
    {
        return PaniniModelInfo.simpleCapsuleName(template);
    }


    private String buildQualifiedCapsuleName()
    {
        return PaniniModelInfo.qualifiedCapsuleName(template);
    }


    private List<String> buildImports()
    {
        return Source.buildCollectedImportDecls(template,
                                                "org.paninij.lang.CapsuleInterface",
                                                "org.paninij.runtime.Panini$Capsule");
    }


    private String buildPackage()
    {
        return context.getPackageOf(template);
    }
    

    /**
     * Returns the `wire()` method declaration, if the interface needs it. Otherwise returns the
     * empty string. Note that the `wire()` declaration includes a trailing semicolon.
     */
    private String buildWireMethodDecl()
    {
        if (PaniniModelInfo.hasWiredFieldDecls(context, template)) {
            return PaniniModelInfo.buildWireMethodDecl(context, template) + ";";
        } else {
            return "";
        }
    }
    

    private List<String> buildExecutableDecls()
    {
        ArrayList<String> decls = new ArrayList<String>();

        for (Element child : template.getEnclosedElements())
        {
            if (PaniniModelInfo.isProcedure(child))
            {
                ExecutableElement method = (ExecutableElement) child;
                String decl = Source.format("#0;", Source.buildExecutableDecl(method));
                decls.add(decl);
            }
        }
        return decls;
    }
}
