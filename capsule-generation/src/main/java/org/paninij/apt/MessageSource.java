package org.paninij.apt;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.paninij.apt.util.DuckShape;
import org.paninij.apt.util.JavaModelInfo;
import org.paninij.apt.util.MessageShape;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;
import org.paninij.apt.util.DuckShape.Category;
import org.paninij.apt.util.SourceFile;
import org.paninij.model.AnnotationKind;
import org.paninij.model.Capsule;
import org.paninij.model.MessageKind;
import org.paninij.model.Procedure;
import org.paninij.model.Type;
import org.paninij.model.Variable;

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
        packs.add(ret.wrapped());

        switch (kind) {
        case ARRAY:
        case DECLARED:
            TypeElement typeElem = (TypeElement) ((DeclaredType) ret.getMirror()).asElement();
            packs = Source.buildCollectedImportDecls(typeElem, packs);
            return packs;
        case BOOLEAN:
        case BYTE:
        case CHAR:
        case DOUBLE:
        case FLOAT:
        case INT:
        case LONG:
        case SHORT:
        case VOID:
            return Source.buildImportDecls(packs);
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