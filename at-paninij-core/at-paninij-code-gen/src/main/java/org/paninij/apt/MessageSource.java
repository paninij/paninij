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
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

import org.paninij.apt.model.Procedure;
import org.paninij.apt.model.Type;
import org.paninij.apt.model.Variable;
import org.paninij.apt.util.MessageShape;
import org.paninij.apt.util.Source;
import org.paninij.apt.util.SourceFile;

public abstract class MessageSource
{

    protected Procedure context;
    protected MessageShape shape;

    protected abstract SourceFile generate(Procedure procedure);
    protected abstract String generateContent();

    protected void setContext(Procedure procedure) {
        this.context = procedure;
        this.shape = new MessageShape(procedure);
    }

    protected List<String> buildParameterFields() {
        List<String> fields = new ArrayList<String>();
        int i = 0;
        for (Variable v : this.context.getParameters()) {
            fields.add("public " + v.slot() + " panini$arg" + (i++) + ";");
        }
        return fields;
    }

    public String buildQualifiedClassName() {
        return this.shape.getPackage() + "." + this.shape.encoded;
    }

    protected List<String> buildImports() {
        return this.buildImports(new ArrayList<String>());
    }

    protected List<String> buildImports(List<String> extra) {
        Type ret = this.context.getReturnType();
        TypeKind kind = ret.getKind();

        List<String> packs = new ArrayList<String>(extra);

        packs.add("org.paninij.runtime.Panini$Future");
        packs.add("org.paninij.runtime.Panini$Message");
        packs.add(ret.packed());

        switch (this.shape.category) {
        case DUCKFUTURE:
            TypeElement typeElem = (TypeElement) ((DeclaredType) ret.getMirror()).asElement();
            packs = Source.buildCollectedImportDecls(typeElem, packs);
            return packs;
        case FUTURE:
        case PREMADE:
        case SIMPLE:
            return Source.buildImportDecls(packs);
        case ERROR:
        default:
            String msg = "The given `return` (of the form `#0`) has an unexpected `TypeKind`: #1";
            msg = Source.format(msg, ret, kind);
            throw new IllegalArgumentException(msg);
        }
    }

    protected List<String> buildConstructor() {
        return this.buildConstructor("");
    }

    protected List<String> buildConstructor(String prependToBody) {
        // Create a list of parameters to the constructor starting with the `procID`.
        List<String> params = new ArrayList<String>();
        params.add("int procID");

        // Create a list of initialization statements.
        List<String> initializers = new ArrayList<String>();
        initializers.add("panini$procID = procID;");

        int i = 0;
        for (Variable var : this.context.getParameters()) {
            params.add(var.slot() + " arg" + (i));
            initializers.add(Source.format("panini$arg#0 = arg#0;", i));
            i++;
        }

        List<String> src = Source.lines("public #0(#1)",
                                        "{",
                                        "    #2",
                                        "    ##",
                                        "}");

        src = Source.formatAll(src, this.shape.encoded,
                                    String.join(", ", params),
                                    prependToBody);
        src = Source.formatAlignedFirst(src, initializers);

        return src;
    }
}