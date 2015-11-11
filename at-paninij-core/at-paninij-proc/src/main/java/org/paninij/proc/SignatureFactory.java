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
package org.paninij.proc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.paninij.proc.model.Procedure;
import org.paninij.proc.model.Variable;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.Source;

public class SignatureFactory extends SignatureArtifactFactory
{
    @Override
    protected String generateContent() {
        String src = Source.cat(
                "package #0;",
                "",
                "##",
                "",
                "#1",
                "@SuppressWarnings(\"unused\")",  // To suppress unused import warnings.
                "@SignatureInterface",
                "public interface #2",
                "{",
                "    ##",
                "}");

        src = Source.format(src,
                this.signature.getPackage(),
                PaniniProcessor.getGeneratedAnno("SignatureFactory"),
                this.signature.getSimpleName());
        src = Source.formatAligned(src, this.generateImports());
        src = Source.formatAligned(src, this.generateFacades());

        return src;
    }

    protected List<String> generateImports() {
        Set<String> imports = new HashSet<String>();
        imports.add("org.paninij.lang.SignatureInterface");
        imports.add("javax.annotation.Generated");

        for (Procedure p : this.signature.getProcedures()) {
            MessageShape shape = new MessageShape(p);
            imports.add(shape.getPackage() + "." +shape.encoded);
        }

        imports.addAll(this.signature.getImports());

        List<String> prefixedImports = new ArrayList<String>();

        for (String i : imports) {
            prefixedImports.add("import " + i + ";");
        }

        return prefixedImports;
    }

    protected List<String> generateFacades() {
        List<String> facades =  new ArrayList<String>();

        for (Procedure p : this.signature.getProcedures()) {
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

    @Override
    protected String getQualifiedName()
    {
        return signature.getQualifiedName();
    }
}
