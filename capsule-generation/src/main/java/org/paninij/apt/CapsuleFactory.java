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
 * Contributor(s): Dalton Mills
 */
package org.paninij.apt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.paninij.apt.util.MessageShape;
import org.paninij.apt.util.Source;
import org.paninij.apt.util.SourceFile;
import org.paninij.model.Capsule;
import org.paninij.model.Procedure;
import org.paninij.model.Variable;

public class CapsuleFactory {

    private Capsule context;

    public SourceFile make(Capsule capsule) {
        this.context = capsule;

        String name = this.context.getQualifiedName();
        String content = this.generateContent();

        return new SourceFile(name, content);
    }

    private String generateContent() {
        String src = Source.cat(
                "package #0;",
                "",
                "##",
                "",
                "@CapsuleInterface",
                "public interface #1 extends #2",
                "{",
                "    #3",
                "    ##",
                "}");

        src = Source.format(src,
                this.context.getPackage(),
                this.context.getSimpleName(),
                this.generateInterfaces(),
                this.generateWiredDecl());

        src = Source.formatAligned(src, this.generateImports());
        src = Source.formatAligned(src, this.generateFacades());

        return src;
    }

    protected String generateInterfaces() {
        List<String> interfaces = this.context.getSignatures();
        interfaces.add("Panini$Capsule");
        return String.join(", ", interfaces);
    }

    protected String generateWiredDecl() {
        List<String> decls = new ArrayList<String>();

        for (Variable v : this.context.getWired()) {
            decls.add(v.toString());
        }

        return decls.isEmpty() ? "" : Source.format("public void wire(#0);", String.join(", ", decls));
    }

    protected List<String> generateImports() {
        Set<String> imports = new HashSet<String>();

        for (Procedure p : this.context.getProcedures()) {
            MessageShape shape = new MessageShape(p);
            imports.add(shape.getPackage() + "." + shape.encoded);
        }

        imports.addAll(this.context.getImports());

        imports.add("java.util.concurrent.Future");
        imports.add("org.paninij.lang.CapsuleInterface");
        imports.add("org.paninij.runtime.Panini$Capsule");

        List<String> prefixedImports = new ArrayList<String>();

        for (String i : imports) {
            prefixedImports.add("import " + i + ";");
        }

        return prefixedImports;
    }

    protected List<String> generateFacades() {
        List<String> facades =  new ArrayList<String>();

        for (Procedure p : this.context.getProcedures()) {
            facades.add(this.generateFacade(p));
            facades.add("");
        }

        return facades;
    }

    protected String generateFacade(Procedure p) {
        MessageShape shape = new MessageShape(p);

        List<String> argDecls = new ArrayList<String>();

        for (Variable v : p.getParameters()) {
            argDecls.add(v.toString());
        }

        String argDeclString = String.join(", ", argDecls);

        String declaration = Source.format("public #0 #1(#2);",
                shape.realReturn,
                p.getName(),
                argDeclString);

        return declaration;
    }
}
