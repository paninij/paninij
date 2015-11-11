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
import org.paninij.proc.model.Type;
import org.paninij.proc.model.Variable;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.PaniniModel;
import org.paninij.proc.util.Source;

public class CapsuleMonitorFactory extends CapsuleProfileFactory
{

    public static final String CAPSULE_PROFILE_MONITOR_SUFFIX = "$Monitor";

    @Override
    protected String getQualifiedName()
    {
        return this.capsule.getQualifiedName() + CAPSULE_PROFILE_MONITOR_SUFFIX;
    }

    @Override
    protected String generateContent()
    {
        String src = Source.cat(
                "package #0;",
                "",
                "##",
                "",
                "#1",
                "@SuppressWarnings(\"unused\")",  // To suppress unused import warnings.
                "public class #2 extends Capsule$Monitor implements #3",
                "{",
                "    ##",
                "}");

        src = Source.format(src,
                this.capsule.getPackage(),
                PaniniProcessor.getGeneratedAnno("CapsuleMonitorFactory"),
                this.generateClassName(),
                this.capsule.getSimpleName());

        src = Source.formatAligned(src, generateImports());
        src = Source.formatAligned(src, generateCapsuleBody());

        return src;
    }

    @Override
    protected String generateClassName()
    {
        return this.capsule.getSimpleName() + CAPSULE_PROFILE_MONITOR_SUFFIX;
    }

    private List<String> generateImports()
    {
        Set<String> imports = new HashSet<String>();

        for (Procedure p : this.capsule.getProcedures()) {
            MessageShape shape = new MessageShape(p);
            imports.add(shape.fullLocation());
        }

        imports.addAll(this.capsule.getImports());
        imports.add("javax.annotation.Generated");
        imports.add("java.util.concurrent.Future");
        imports.add("org.paninij.runtime.Capsule$Monitor");
        imports.add("org.paninij.runtime.Panini$Capsule");
        imports.add("org.paninij.runtime.Panini$Message");
        imports.add("org.paninij.runtime.Panini$Future");
        imports.add("org.paninij.runtime.Panini$System");
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

    @Override
    protected String generateProcedureDecl(MessageShape shape) {
        List<String> argDecls = this.generateProcArgumentDecls(shape.procedure);
        String argDeclString = String.join(", ", argDecls);
        String declaration = Source.format("public synchronized #0 #1(#2)",
                shape.realReturn,
                shape.procedure.getName(),
                argDeclString);
        List<String> thrown = shape.procedure.getThrown();
        declaration += (thrown.isEmpty()) ? "" : " throws " + String.join(", ", thrown);
        return declaration;
    }

    @Override
    protected List<String> generateProcedure(Procedure procedure) {
        MessageShape shape = new MessageShape(procedure);

        List<String> source = Source.lines(
                "@Override",
                "#0",
                "{",
                "   ##",
                "}",
                "");
        source = Source.formatAll(source,
                this.generateProcedureDecl(shape));

        return Source.formatAlignedFirst(source, this.generateEncapsulatedMethodCall(shape));
    }

    private List<String> generateEncapsulatedMethodCall(MessageShape shape)
    {
        List<String> encap = new ArrayList<String>();
        List<String> argNames = this.generateProcArgumentNames(shape.procedure);
        String args = String.join(", ", argNames);
        String call = "panini$encapsulated." + shape.procedure.getName() + "(" + args + ")";
        switch(shape.behavior) {
        case UNBLOCKED_DUCK:
        case BLOCKED_FUTURE:
            String ret = shape.returnType.isVoid() ? "" : "return ";
            encap.add(ret + call + ";");
            return encap;
        case UNBLOCKED_PREMADE:
        case BLOCKED_PREMADE:
            encap.add("return " + call + ";");
            return encap;
        case ERROR:
            break;
        case UNBLOCKED_FUTURE:
            argNames.add(0, "-1");
            args = String.join(", ", argNames);
            encap.add(shape.encoded + " msg = new " + shape.encoded + "(" + args + ");");
            Type r = shape.procedure.getReturnType();
            if (r.isVoid()) {
                encap.add(call + ";");
                encap.add("msg.panini$resolve(null);");
            } else {
                encap.add(r.wrapped() + " result = " + call + ";");
                encap.add("msg.panini$resolve(result);");
            }
            encap.add("return msg;");
            return encap;
        case UNBLOCKED_SIMPLE:
            encap.add(call + ";");
            return encap;
        default:
            break;
        }
        return encap;
    }

    private List<String> generateProcedures()
    {
        ArrayList<String> src = new ArrayList<String>();
        for (Procedure p : this.capsule.getProcedures()) {
            src.addAll(this.generateProcedure(p));
        }
        return src;
    }

    private List<String> generateInitLocals()
    {
        List<Variable> locals = this.capsule.getLocalFields();
        List<String> source = new ArrayList<String>();

        if (locals.size() == 0) return source;

        for (Variable local : locals) {
            if (local.isArray()) {
                List<String> lines = Source.lines(
                        "for (int i = 0; i < panini$encapsulated.#0.length; i++) {",
                        "    panini$encapsulated.#0[i] = new #1#2();",
                        "}",
                        "");
                source.addAll(Source.formatAll(
                        lines,
                        local.getIdentifier(),
                        local.getEncapsulatedType(),
                        CAPSULE_PROFILE_MONITOR_SUFFIX));
            } else {
                source.add(Source.format(
                        "panini$encapsulated.#0 = new #1#2();",
                        local.getIdentifier(),
                        local.raw(),
                        CAPSULE_PROFILE_MONITOR_SUFFIX));
            }
        }


        for (Variable local : locals) {
            if (local.isArray()) {
                List<String> lines = Source.lines(
                        "for (int i = 0; i < panini$encapsulated.#0.length; i++) {",
                        "    ((Panini$Capsule) panini$encapsulated.#0[i]).panini$openLink();",
                        "}");
                source.addAll(Source.formatAll(
                        lines,
                        local.getIdentifier()));
            } else {
                source.add(Source.format(
                        "((Panini$Capsule) panini$encapsulated.#0).panini$openLink();",
                        local.getIdentifier()));
            }
        }

        if (this.capsule.hasDesign()) {
            source.add("panini$encapsulated.design(this);");
        }

        for (Variable local : locals) {
            if (local.isArray()) {
                List<String> src = Source.lines(
                        "for (int i = 0; i < panini$encapsulated.#0.length; i++) {",
                        "    panini$encapsulated.#0[i].panini$start();",
                        "}");
                source.addAll(Source.formatAll(src, local.getIdentifier()));
            } else {
                source.add(Source.format(
                        "panini$encapsulated.#0.panini$start();",
                        local.getIdentifier()));
            }
        }

        List<String> decl = Source.lines(
                "@Override",
                "protected void panini$initLocals() {",
                "    ##",
                "}",
                "");

        return Source.formatAlignedFirst(decl, source);
    }

    private List<String> generateRun()
    {
        if (this.capsule.isActive()) {
            return Source.lines(
                    "@Override",
                    "public void run() {",
                    "    try {",
                    "        panini$checkRequiredFields();",
                    "        panini$initLocals();",
                    "        panini$initState();",
                    "        panini$encapsulated.run();",
                    "    } catch (Throwable thrown) {",
                    "        panini$errors.add(thrown);",
                    "    } finally {",
                    "        panini$onTerminate();",
                    "        try {",
                    "           Panini$System.threads.countDown();",
                    "        } catch (InterruptedException e) {",
                    "            e.printStackTrace();",
                    "        }",
                    "    }",
                    "}",
                    "");
        }

        return Source.lines(
                "@Override",
                "@SuppressWarnings(\"unchecked\")",
                "public void run() {",
                "    try {",
                "        panini$checkRequiredFields();",
                "        panini$initLocals();",
                "        panini$initState();",
                "    } catch (Throwable thrown) {",
                "        panini$errors.add(thrown);",
                "    }",
                "}",
                "");
    }

    private List<String> generateCapsuleBody()
    {
        List<String> src = new ArrayList<String>();

        src.add(this.generateEncapsulatedDecl());
        src.addAll(this.generateProcedures());
        src.addAll(this.generateCheckRequiredFields());
        src.addAll(this.generateExport());
        src.addAll(this.generateInitLocals());
        src.addAll(this.generateInitState());
        src.addAll(this.generateOnTerminate());
        src.addAll(this.generateGetAllState());
        src.addAll(this.generateRun());
        src.addAll(this.generateMain());

        return src;
    }

}
