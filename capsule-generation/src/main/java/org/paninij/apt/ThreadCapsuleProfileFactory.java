package org.paninij.apt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.paninij.apt.util.JavaModelInfo;
import org.paninij.apt.util.MessageShape;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;
import org.paninij.apt.util.SourceFile;
import org.paninij.model.Capsule;
import org.paninij.model.Procedure;
import org.paninij.model.Type;
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
        imports.add("org.paninij.runtime.Panini$Message");
        imports.add("org.paninij.runtime.Panini$Future");
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
                "public void panini$checkRequired() {",
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
                "public void wire(#0) {",
                "    ##",
                "}",
                "");

        src = Source.formatAll(src, String.join(", ", decls));
        src = Source.formatAlignedFirst(src, assignments);

        return src;
    }

    private List<String> generateInitChildren() {
        List<Variable> children = this.context.getChildren();
        List<String> source = new ArrayList<String>();

        if (children.size() == 0) return source;

        for (Variable child : children) {
            if (child.isArray()) {
                List<String> lines = Source.lines(
                        "for (int i = 0; i < panini$encapsulated.#0.length; i++) {",
                        "    panini$encapsulated.#0[i].panini$start();",
                        "}");
                source.addAll(Source.formatAll(lines,  child.getIdentifier()));
            } else {
                source.add(Source.format(
                        "panini$encapsulated.#0.panini$start();",
                        child.getIdentifier()));
            }
        }

        List<String> decl = Source.lines(
                "@Override",
                "protected void panini$initChildren() {",
                "    ##",
                "}",
                "");

        return Source.formatAlignedFirst(decl, source);
    }

    private List<String> generateInitState() {
        if (this.context.hasInit()) {
            return Source.lines(
                    "@Override",
                    "protected void panini$initState() {",
                    "    panini$encapsulated.init();",
                    "}",
                    "");
        } else {
            return new ArrayList<String>();
        }
    }

    private List<String> generateRun() {
        List<String> source = new ArrayList<String>();

        if (this.context.isActive()) {
            return Source.lines(
                    "@Override",
                    "public void run() {",
                    "    try {",
                    "        panini$checkRequired();",
                    "        panini$initChildren();",
                    "        panini$initState();",
                    "        panini$encapsulated.run();",
                    "    } finally {",
                    "        // TODO",
                    "    }",
                    "}",
                    "");
        }

        List<String> src = Source.lines(
                "@Override",
                "@SuppressWarnings(\"unchecked\")",
                "public void run() {",
                "    try {",
                "        panini$checkRequired();",
                "        panini$initChildren();",
                "        panini$initState();",
                "",
                "        boolean terminate = false;",
                "        while (!terminate) {",
                "            Panini$Message msg = panini$nextMessage();",
                "            ##",
                "        }",
                "    }",
                "    catch (Exception ex) { /* do nothing for now */ }",
                "}"
            );
        return Source.formatAlignedFirst(src, buildRunSwitch());
    }

    private List<String> buildRunSwitch() {
        List<String> lines = new ArrayList<String>();
        lines.add("switch(msg.panini$msgID()) {");

        // Add a case statement for each procedure wrapper.
        for (Procedure p : this.context.getProcedures()) {
            lines.addAll(this.buildRunSwitchCase(p));
        }

        lines.addAll(Source.lines(
                "case PANINI$SHUTDOWN:",
                "    if (panini$isEmpty() == false) {",
                "        panini$push(msg);",
                "    } else {",
                "        terminate = true;",
                "    }",
                "    break;",
                "",
                "case PANINI$EXIT:",
                "    terminate = true;",
                "    break;",
                "}"));
        return lines;
    }

    private List<String> buildRunSwitchCase(Procedure procedure) {
        MessageShape shape = new MessageShape(procedure);

        // `duck` will need to be resolved if and only if `procedure` has a return value.
        if (shape.category == MessageShape.Category.SIMPLE) {
            // Simply call the template isntance's method with the args encapsulated in the duck.
            List<String> src = Source.lines(
                    "case #0:",
                    "    #1;",
                    "    break;");

            return Source.formatAll(src,
                    this.generateProcedureID(procedure),
                    this.generateEncapsulatedMethodCall(shape));
        }

        Type r = procedure.getReturnType();
        // A void wrapper cannot be instantiated, so we have to resolve with null
        if (r.isVoid()) {
            // Call the template instance's method and resolve the duck using null.
            List<String> src = Source.lines("case #0:",
                                            "    #1;",
                                            "    ((Panini$Future<#2>) msg).panini$resolve(null);",
                                            "    break;");
            return Source.formatAll(src,
                    this.generateProcedureID(procedure),
                    this.generateEncapsulatedMethodCall(shape),
                    procedure.getReturnType().wrapped());
        } else {
            // Call the template instance's method and resolve the duck using the result.
            List<String> src = Source.lines("case #0:",
                                            "    ((Panini$Future<#1>) msg).panini$resolve(#2);",
                                            "    break;");
            return Source.formatAll(src,
                    this.generateProcedureID(procedure),
                    procedure.getReturnType().wrapped(),
                    this.generateEncapsulatedMethodCall(shape));
        }
    }

    private String generateEncapsulatedMethodCall(MessageShape shape) {
        List<String> args = new ArrayList<String>();

        // Generate the list of types defined on the `method`. The `null` value is used to
        // represent a parameter type whenever that type is primitive.
        List<String> paramTypes = new ArrayList<String>();
        for (Variable v : shape.procedure.getParameters()) {
            paramTypes.add(v.isPrimitive() ? null : v.raw());
        }

        // Extract each argument held in the duck. For each of these extractions, one type cast is
        // used to convert the `Panini$Message` to a concrete duck type. If the duck is storing
        // an object in an `Object` box, then another type cast is used to convert that argument to
        // its original type.
        for (int i = 0; i < paramTypes.size(); i++) {
            String paramType = paramTypes.get(i);
            args.add(Source.format(
                    "#0((#1) msg).panini$arg#2",
                     paramType == null ? "" : "(" + paramType + ") ",
                     shape.encoded,
                     i));
        }

        return Source.format(
                "panini$encapsulated.#0(#1)",
                shape.procedure.getName(),
                String.join(", ", args));
    }

    private List<String> generateCapsuleBody() {
        List<String> src = new ArrayList<String>();

        src.add(this.generateEncapsulatedDecl());
        src.addAll(this.generateProcedureIDs());
        src.addAll(this.generateProcedures());
        src.addAll(this.generateRequiredFields());
        src.addAll(this.generateWire());
        src.addAll(this.generateInitChildren());
        src.addAll(this.generateInitState());
        src.addAll(this.generateRun());

        return src;
    }

}
