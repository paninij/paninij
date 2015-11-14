/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/

package org.paninij.proc.factory;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

import org.paninij.proc.model.Procedure;
import org.paninij.proc.model.Type;
import org.paninij.proc.model.Variable;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.Source;

public abstract class AbstractMessageFactory implements ArtifactFactory<Procedure>
{
    protected Procedure context;
    protected MessageShape shape;

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
        packs.add("javax.annotation.Generated");
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