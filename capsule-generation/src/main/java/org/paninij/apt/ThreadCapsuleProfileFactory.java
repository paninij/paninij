package org.paninij.apt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import org.paninij.apt.util.MessageShape;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;
import org.paninij.apt.util.SourceFile;
import org.paninij.model.Capsule;
import org.paninij.model.Procedure;
import org.paninij.model.Variable;

public class ThreadCapsuleProfileFactory extends CapsuleProfileFactory
{

    public static final String CAPSULE_PROFILE_THREAD_SUFFIX = "$Thread$Temp";

    private Capsule context;

    @Override
    public SourceFile make(Capsule capsule) {
        this.context = capsule;

        String name = this.generateFileName();
        String content = this.generateContent();

        return new SourceFile(name, content);
    }

    private String generateFileName() {
        return this.context.getQualifiedName() + CAPSULE_PROFILE_THREAD_SUFFIX;
    }

    private String generateContent() {
        String src = Source.cat(
                "package #0;",
                "",
                "##",
                "",
                "public class #1 extends Capsule$Thread implements #2",
                "{",
                "    ##",
                "}");

        src = Source.format(src,
                this.context.getPackage(),
                this.generateClassName(),
                this.context.getSimpleName());

        src = Source.formatAligned(src, generateImports());
        src = Source.formatAligned(src, generateCapsuleBody());

        return src;
    }

    private String generateClassName() {
        return this.context.getSimpleName() + CAPSULE_PROFILE_THREAD_SUFFIX;
    }

    private List<String> generateImports() {
        Set<String> imports = new HashSet<String>();

        for (Procedure p : this.context.getProcedures()) {
            MessageShape shape = new MessageShape(p);
            imports.add(shape.getPackage() + "." +shape.encoded);
        }

        imports.addAll(this.context.getImports());

        imports.add("java.util.concurrent.Future");
        imports.add("org.paninij.runtime.Capsule$Thread");
        imports.add(this.context.getQualifiedName());

        List<String> prefixedImports = new ArrayList<String>();

        for (String i : imports) {
            prefixedImports.add("import " + i + ";");
        }

        return prefixedImports;
    }

    private String generateEncapsulatedDecl() {
        return Source.format(
                "private #0 panini$encapsulated = new #0();",
                this.context.getQualifiedName() + PaniniModelInfo.CAPSULE_TEMPLATE_SUFFIX);
    }

    private List<String> generateProcedureIDs() {
        ArrayList<String> decls = new ArrayList<String>();
        int currID = 0;

        for (Procedure p : this.context.getProcedures()) {
            decls.add(Source.format("public static final int #0 = #1;",
                    generateProcedureID(p),
                    currID++));
        }

        return decls;
    }

    private List<String> generateProcedures() {
        ArrayList<String> src = new ArrayList<String>();
        for (Procedure p : this.context.getProcedures()) {
            src.addAll(this.generateProcedure(p));
        }
        return src;
    }

    private List<String> generateRequiredFields() {
        // Get the fields which must be non-null, i.e. all wired fields and all arrays of children.
        List<Variable> required = this.context.getWired();

        for (Variable child : this.context.getChildren()) {
            if (child.isArray()) required.add(child);
        }

        if (required.isEmpty()) return new ArrayList<String>();

        List<String> assertions = new ArrayList<String>(required.size());
        for (int idx = 0; idx < required.size(); idx++) {
            assertions.add(Source.format(
                    "assert(panini$encapsulated.#0 != null);",
                    required.get(idx).getIdentifier()));
        }

        List<String> lines = Source.lines(
                "@Override",
                "public void panini$checkRequired()",
                "{",
                "    ##",
                "}",
                "");
        return Source.formatAlignedFirst(lines, assertions);
    }

    private List<String> generateWire() {
        List<Variable> wired = this.context.getWired();
        List<String> assignments = new ArrayList<String>();
        List<String> decls = new ArrayList<String>();

        if (wired.isEmpty()) return assignments;

        for (Variable var : wired) {
            String instantiation = Source.format("panini$encapsulated.#0 = #0;", var.getIdentifier());
            assignments.add(instantiation);
            decls.add(var.toString());
        }

        List<String> src = Source.lines(
                "@Override",
                "public void wire(#0)",
                "{",
                "    ##",
                "}",
                "");

        src = Source.formatAll(src, String.join(", ", decls));
        src = Source.formatAlignedFirst(src, assignments);

        return src;
    }

    private List<String> generateCapsuleBody() {
        List<String> src = new ArrayList<String>();

        src.add(this.generateEncapsulatedDecl());
        src.addAll(this.generateProcedureIDs());
        src.addAll(this.generateProcedures());
        src.addAll(this.generateRequiredFields());
        src.addAll(this.generateWire());

        return src;
    }

}
