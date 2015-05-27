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
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;
import org.paninij.apt.util.DuckShape.Category;
import org.paninij.apt.util.SourceFile;
import org.paninij.model.AnnotationKind;
import org.paninij.model.Capsule;
import org.paninij.model.MessageKind;
import org.paninij.model.Procedure;
import org.paninij.model.Variable;

public abstract class MessageSource
{

    protected Procedure context;

    protected abstract SourceFile generate(Procedure procedure);
    protected abstract String generateContent();
    protected abstract String encode();
    protected abstract String buildPackage();
    protected abstract List<String> buildConstructor(String prependToBody);

    protected void setContext(Procedure procedure) {
        this.context = procedure;
    }

    protected String encodeReturnType() {
        TypeMirror returnType = this.context.getReturnType();
        return returnType.toString().replaceAll("_", "__").replaceAll("\\.", "_");
    }

    protected String encodeParameters() {
        List<String> slots = this.getSlotTypes();
        return String.join("$", slots);
    }

    public String encode(Procedure context) {
        Procedure currentContext = this.context;
        this.setContext(context);
        String encoded = this.encode();
        this.setContext(currentContext);
        return encoded;
    }

    protected List<String> getSlotTypes() {
        List<Variable> args = this.context.getParameters();
        List<String> slots = new ArrayList<String>();
        for (Variable arg : args) {
            slots.add(this.getSlotType(arg));
        }
        return slots;
    }

    protected String getSlotType(Variable variable) {
        // TODO: Look for another way to get the strings in the primitive cases.
        TypeKind kind = variable.getType().getKind();

        switch (kind) {
        case ARRAY:
        case DECLARED:
            return "ref";
        case BOOLEAN:
            return "boolean";
        case BYTE:
            return "byte";
        case CHAR:
            return "char";
        case DOUBLE:
            return "double";
        case FLOAT:
            return "float";
        case INT:
            return "int";
        case LONG:
            return "long";
        case SHORT:
            return "short";
        default:
            String msg = "The given `param` (of the form `#0`) has an unexpected `TypeKind`: #1";
            msg = Source.format(msg, variable, kind);
            throw new IllegalArgumentException(msg);
        }
    }

    protected String wrapReturnType() {
        TypeMirror mirror = this.context.getReturnType();
        TypeKind kind = mirror.getKind();

        switch (kind) {
        case ARRAY:
        case DECLARED:
            return mirror.toString();
        case BOOLEAN:
            return "java.lang.Boolean";
        case BYTE:
            return "java.lang.Byte";
        case CHAR:
            return "java.lang.Char";
        case DOUBLE:
            return "java.lang.Double";
        case FLOAT:
            return "java.lang.Float";
        case INT:
            return "java.lang.Integer";
        case LONG:
            return "java.lang.Long";
        case SHORT:
            return "java.lang.Short";
        case VOID:
            return "java.lang.Void";
        default:
            String msg = "The given `return` (of the form `#0`) has an unexpected `TypeKind`: #1";
            msg = Source.format(msg, mirror, kind);
            throw new IllegalArgumentException(msg);
        }
    }

    protected List<String> buildParameterFields() {
        List<String> slots = this.getSlotTypes();
        List<String> fields = new ArrayList<String>(slots.size());
        int i = 0;
        for (String slot : slots) {
            fields.add("public " + slot + " panini$arg" + (++i) + ";");
        }
        return fields;
    }

    public String buildQualifiedClassName() {
        return this.buildPackage() + "." + this.encode();
    }

    protected List<String> buildImports() {
        return this.buildImports(new ArrayList<String>());
    }

    protected List<String> buildImports(List<String> extra) {
        TypeMirror mirror = this.context.getReturnType();
        TypeKind kind = mirror.getKind();

        List<String> packs = new ArrayList<String>(extra);

        packs.add("org.paninij.runtime.Panini$Future");
        packs.add("org.paninij.runtime.Panini$Message");
        packs.add(this.wrapReturnType());

        switch (kind) {
        case ARRAY:
        case DECLARED:
            TypeElement typeElem = (TypeElement) ((DeclaredType) mirror).asElement();
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
            msg = Source.format(msg, mirror, kind);
            throw new IllegalArgumentException(msg);
        }
    }

    protected List<String> buildConstructor() {
        return this.buildConstructor("");
    }
}