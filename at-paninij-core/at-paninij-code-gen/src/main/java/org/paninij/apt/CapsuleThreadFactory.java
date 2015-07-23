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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.type.TypeKind;

import org.paninij.apt.model.Procedure;
import org.paninij.apt.model.Type;
import org.paninij.apt.model.Variable;
import org.paninij.apt.util.MessageShape;
import org.paninij.apt.util.PaniniModel;
import org.paninij.apt.util.Source;

public class CapsuleThreadFactory extends CapsuleProfileFactory
{
    public static final String CAPSULE_PROFILE_THREAD_SUFFIX = "$Thread";

    @Override
    protected String getQualifiedName()
    {
        return this.capsule.getQualifiedName() + CAPSULE_PROFILE_THREAD_SUFFIX;
    }

    @Override
    protected String generateContent()
    {
        String src = Source.cat(
                "package #0;",
                "",
                "##",
                "",
                "@SuppressWarnings(\"unused\")",  // To suppress unused import warnings.
                "@CapsuleThread",
                "public class #1 extends Capsule$Thread implements #2",
                "{",
                "    ##",
                "}");

        src = Source.format(src,
                this.capsule.getPackage(),
                this.generateClassName(),
                this.capsule.getSimpleName());

        src = Source.formatAligned(src, generateImports());
        src = Source.formatAligned(src, generateCapsuleBody());

        return src;
    }

    private String generateClassName()
    {
        return this.capsule.getSimpleName() + CAPSULE_PROFILE_THREAD_SUFFIX;
    }

    private List<String> generateImports()
    {
        Set<String> imports = new HashSet<String>();

        for (Procedure p : this.capsule.getProcedures()) {
            MessageShape shape = new MessageShape(p);
            imports.add(shape.getPackage() + "." +shape.encoded);
        }

        imports.addAll(this.capsule.getImports());

        imports.add("java.util.concurrent.Future");
        imports.add("org.paninij.lang.CapsuleThread");
        imports.add("org.paninij.runtime.Capsule$Thread");
        imports.add("org.paninij.runtime.Panini$Capsule");
        imports.add("org.paninij.runtime.Panini$Message");
        imports.add("org.paninij.runtime.Panini$Future");
        imports.add("org.paninij.runtime.Panini$System");
        imports.add("org.paninij.runtime.check.DynamicOwnershipTransfer");
        imports.add(this.capsule.getQualifiedName());

        List<String> prefixedImports = new ArrayList<String>();

        for (String i : imports) {
            prefixedImports.add("import " + i + ";");
        }

        return prefixedImports;
    }

    private String generateEncapsulatedDecl()
    {
        return Source.format(
                "private #0 panini$encapsulated = new #0();",
                this.capsule.getQualifiedName() + PaniniModel.CAPSULE_TEMPLATE_SUFFIX);
    }

    private List<String> generateProcedureIDs()
    {
        ArrayList<String> decls = new ArrayList<String>();
        int currID = 0;

        for (Procedure p : this.capsule.getProcedures()) {
            decls.add(Source.format("public static final int #0 = #1;",
                    generateProcedureID(p),
                    currID++));
        }

        decls.add("");

        return decls;
    }

    private List<String> generateProcedures()
    {
        ArrayList<String> src = new ArrayList<String>();
        for (Procedure p : this.capsule.getProcedures()) {
            src.addAll(this.generateProcedure(p));
        }
        return src;
    }

    private List<String> generateCheckRequiredFields()
    {
        // Get the fields which must be non-null, i.e. all wired fields and all arrays of children.
        List<Variable> required = this.capsule.getWired();

        for (Variable child : this.capsule.getChildren()) {
            if (child.isArray()) required.add(child);
        }

        if (required.isEmpty()) return new ArrayList<String>();

        List<String> assertions = new ArrayList<String>(required.size());
        for (int idx = 0; idx < required.size(); idx++) {
            if (required.get(idx).isCapsule()) {
                assertions.add(Source.format(
                        "assert(panini$encapsulated.#0 != null);",
                        required.get(idx).getIdentifier()));
            }
        }

        List<String> lines = Source.lines(
                "@Override",
                "public void panini$checkRequiredFields() {",
                "    ##",
                "}",
                "");
        return Source.formatAlignedFirst(lines, assertions);
    }

    private List<String> generateWire()
    {
        List<Variable> wired = this.capsule.getWired();
        List<String> refs = new ArrayList<String>();
        List<String> decls = new ArrayList<String>();

        if (wired.isEmpty()) return refs;

        for (Variable var : wired) {
            String instantiation = Source.format("panini$encapsulated.#0 = #0;", var.getIdentifier());
            refs.add(instantiation);

            if (var.isArray()) {
                if (var.getEncapsulatedType().isCapsule()) {
                    List<String> lines = Source.lines(
                            "for (int i = 0; i < panini$encapsulated.#0.length; i++) {",
                            "    ((Panini$Capsule) panini$encapsulated.#0[i]).panini$openLink();",
                            "}");
                    refs.addAll(Source.formatAll(
                            lines,
                            var.getIdentifier()));
                }
            } else {
                if (var.isCapsule()) {
                    refs.add(Source.format("((Panini$Capsule) panini$encapsulated.#0).panini$openLink();", var.getIdentifier()));
                }
            }

            decls.add(var.toString());
        }

        List<String> src = Source.lines(
                "public void wire(#0) {",
                "    ##",
                "}",
                "");

        src = Source.formatAll(src, String.join(", ", decls));
        src = Source.formatAlignedFirst(src, refs);

        return src;
    }

    private List<String> generateInitChildren()
    {
        List<Variable> children = this.capsule.getChildren();
        List<String> source = new ArrayList<String>();

        if (children.size() == 0) return source;

        for (Variable child : children) {
            if (child.isArray()) {
                List<String> lines = Source.lines(
                        "for (int i = 0; i < panini$encapsulated.#0.length; i++) {",
                        "    panini$encapsulated.#0[i] = new #1#2();",
                        "}",
                        "");
                source.addAll(Source.formatAll(
                        lines,
                        child.getIdentifier(),
                        child.getEncapsulatedType(),
                        CAPSULE_PROFILE_THREAD_SUFFIX));
            } else {
                source.add(Source.format(
                        "panini$encapsulated.#0 = new #1#2();",
                        child.getIdentifier(),
                        child.raw(),
                        CAPSULE_PROFILE_THREAD_SUFFIX));
            }
        }


        for (Variable child : children) {
            if (child.isArray()) {
                List<String> lines = Source.lines(
                        "for (int i = 0; i < panini$encapsulated.#0.length; i++) {",
                        "    ((Panini$Capsule) panini$encapsulated.#0[i]).panini$openLink();",
                        "}");
                source.addAll(Source.formatAll(
                        lines,
                        child.getIdentifier()));
            } else {
                source.add(Source.format(
                        "((Panini$Capsule) panini$encapsulated.#0).panini$openLink();",
                        child.getIdentifier()));
            }
        }

        if (this.capsule.hasDesign()) {
            source.add("panini$encapsulated.design(this);");
        }

        for (Variable child : children) {
            if (child.isArray()) {
                List<String> src = Source.lines(
                        "for (int i = 0; i < panini$encapsulated.#0.length; i++) {",
                        "    panini$encapsulated.#0[i].panini$start();",
                        "}");
                source.addAll(Source.formatAll(src, child.getIdentifier()));
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

    private List<String> generateInitState()
    {
        if (!this.capsule.hasInit()) return new ArrayList<String>();
        return Source.lines(
                "@Override",
                "protected void panini$initState() {",
                "    panini$encapsulated.init();",
                "}",
                "");
    }

    List<String> generateGetAllState()
    {
        List<String> states = capsule.getState()
                                     .stream()
                                     .filter(s -> s.getKind() == TypeKind.ARRAY
                                               || s.getKind() == TypeKind.DECLARED)
                                     .map(s -> "panini$encapsulated." + s.getIdentifier())
                                     .collect(Collectors.toList());

        List<String> src = Source.lines("@Override",
                                        "public Object panini$getAllState()",
                                        "{",
                                        "    Object[] state = {#0};",
                                        "    return state;",
                                        "}",
                                        "");

        return Source.formatAll(src, String.join(", ", states));
    }

    private List<String> generateRun()
    {
        if (this.capsule.isActive()) {
            return Source.lines(
                    "@Override",
                    "public void run() {",
                    "    Panini$System.self.set(this);",
                    "    try {",
                    "        panini$checkRequiredFields();",
                    "        panini$initChildren();",
                    "        panini$initState();",
                    "        panini$encapsulated.run();",
                    "    } catch (Throwable thrown) {",
                    "        panini$errors.add(thrown);",
                    "    } finally {",
                    "        panini$onTerminate();",
                    "    }",
                    "}",
                    "");
        }

        List<String> src = Source.lines(
                "@Override",
                "@SuppressWarnings(\"unchecked\")",
                "public void run() {",
                "    Panini$System.self.set(this);",
                "    try {",
                "        panini$checkRequiredFields();",
                "        panini$initChildren();",
                "        panini$initState();",
                "",
                "        boolean terminated = false;",
                "        while (!terminated) {",
                "            Panini$Message msg = panini$nextMessage();",
                "            ##",
                "        }",
                "    } catch (Throwable thrown) {",
                "        panini$errors.add(thrown);",
                "    }",
                "}",
                "");

        return Source.formatAlignedFirst(src, generateRunSwitch());
    }

    private List<String> generateRunSwitch()
    {
        List<String> lines = new ArrayList<String>();
        lines.add("switch(msg.panini$msgID()) {");

        // add a case statement for each procedure wrapper.
        for (Procedure p : this.capsule.getProcedures()) {
            lines.addAll(this.generateRunSwitchCase(p));
        }

        // add case statements for when a capsule shuts down and for EXIT command
        lines.addAll(Source.lines(
                "case PANINI$CLOSE_LINK:",
                "    panini$onCloseLink();",
                "    break;",
                "",
                "case PANINI$TERMINATE:",
                "    panini$onTerminate();",
                "    terminated = true;",
                "    break;",
                "}"));
        return lines;
    }

    private List<String> generateOnTerminate() {
        List<String> shutdowns = new ArrayList<String>();
        List<Variable> references = new ArrayList<Variable>();

        references.addAll(this.capsule.getWired());
        references.addAll(this.capsule.getChildren());

        if (references.isEmpty()) return shutdowns;

        for (Variable reference : references) {
            if (reference.isArray()) {
                if (reference.getEncapsulatedType().isCapsule()) {
                    List<String> src = Source.lines(
                            "for (int i = 0; i < panini$encapsulated.#0.length; i++) {",
                            "    ((Panini$Capsule) panini$encapsulated.#0[i]).panini$closeLink();",
                            "}");
                    shutdowns.addAll(Source.formatAll(src, reference.getIdentifier()));
                }
            } else {
                if (reference.isCapsule()) {
                    shutdowns.add(Source.format("((Panini$Capsule) panini$encapsulated.#0).panini$closeLink();", reference.getIdentifier()));
                }
            }
        }

        List<String> src = Source.lines(
                "@Override",
                "protected void panini$onTerminate() {",
                "    ##",
                "}",
                "");

        return Source.formatAlignedFirst(src, shutdowns);
    }

    private List<String> generateRunSwitchCase(Procedure procedure)
    {
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
            List<String> src = Source.lines("case #0: {",
                                            "    #1 result = #2;",
                                            "    #3;",
                                            "    ((Panini$Future<#1>) msg).panini$resolve(result);",
                                            "    break;",
                                            "}");
            return Source.formatAll(src,
                    this.generateProcedureID(procedure),
                    procedure.getReturnType().wrapped(),
                    this.generateEncapsulatedMethodCall(shape),
                    this.generateAssertSafeResultTransfer());
        }
    }

    private String generateEncapsulatedMethodCall(MessageShape shape)
    {
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

    private String generateAssertSafeResultTransfer()
    {
        // TODO: Clean this up!
        /**
        return Source.format(
                "assert DynamicOwnershipTransfer.#0.isSafeTransfer(#1, #2) : #3",
                PaniniProcessor.dynamicOwnershipTransferKind,
                "result",
                "panini$getAllState()",
                "\"Procedure return attempted unsafe ownership transfer.\"");
        */
        return "";
    }

    private boolean deservesMain()
    {
        // if the capsule has external dependencies, it does
        // not deserve a main
        if (!this.capsule.getWired().isEmpty()) return false;

        if (this.capsule.isActive()) {
            // if the capsule is active and has no external deps,
            // it deserves a main
            return true;
        } else {
            // if the capsule has no children, it does not need a main
            // (this is a bogus/dull scenario)
            if (this.capsule.getChildren().isEmpty()) return false;

            // check if any ancestor capsules are active
            if (this.capsule.hasActiveAncestor()) return true;

            // if no child is active, this does not deserve a main
            return false;
        }
    }

    private List<String> generateMain()
    {
        if (!this.deservesMain()) return new ArrayList<String>();

        List<String> src = Source.lines(
                "public static void main(String[] args) {",
                "    #0 root = new #0();",
                "    root.run();",
                "}");

        return Source.formatAll(src, this.generateClassName());
    }

    private List<String> generateCapsuleBody()
    {
        List<String> src = new ArrayList<String>();

        src.add(this.generateEncapsulatedDecl());
        src.addAll(this.generateProcedureIDs());
        src.addAll(this.generateProcedures());
        src.addAll(this.generateCheckRequiredFields());
        src.addAll(this.generateWire());
        src.addAll(this.generateInitChildren());
        src.addAll(this.generateInitState());
        src.addAll(this.generateOnTerminate());
        src.addAll(this.generateGetAllState());
        src.addAll(this.generateRun());
        src.addAll(this.generateMain());

        return src;
    }

}
