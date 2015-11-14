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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.paninij.proc.PaniniProcessor;
import org.paninij.proc.model.Behavior;
import org.paninij.proc.model.Procedure;
import org.paninij.proc.model.Type;
import org.paninij.proc.model.Variable;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.PaniniModel;
import org.paninij.proc.util.Source;

public class CapsuleTaskFactory extends CapsuleProfileFactory
{
    public static final String CAPSULE_PROFILE_TASK_SUFFIX = "$Task";

    @Override
    protected String getQualifiedName()
    {
        return this.capsule.getQualifiedName() + CAPSULE_PROFILE_TASK_SUFFIX;
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
                "public class #2 extends Capsule$Task implements #3",
                "{",
                "    ##",
                "}");

        src = Source.format(src,
                this.capsule.getPackage(),
                PaniniProcessor.getGeneratedAnno(CapsuleTaskFactory.class),
                this.generateClassName(),
                this.capsule.getSimpleName());

        src = Source.formatAligned(src, generateImports());
        src = Source.formatAligned(src, generateCapsuleBody());

        return src;
    }

    @Override
    protected String generateClassName()
    {
        return this.capsule.getSimpleName() + CAPSULE_PROFILE_TASK_SUFFIX;
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
        imports.add("org.paninij.runtime.Capsule$Task");
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

    @Override
    protected List<String> generateProcedure(Procedure procedure) {
        MessageShape shape = new MessageShape(procedure);
        String doBlock = shape.behavior == Behavior.BLOCKED_FUTURE || shape.behavior == Behavior.BLOCKED_PREMADE ? "panini$emptyQueue();" : "";
        String encoding = PaniniModel.isPaniniCustom(shape.returnType.getMirror()) ? shape.returnType.raw() : shape.encoded;
        
        List<String> source = Source.lines(
                "@Override",
                "#0",
                "{",
                "    #1 panini$message = null;",
                "    panini$message = new #1(#2);",
                "    #3;",
                "    panini$push(panini$message);",
                "    #4",
                "    #5",
                "}",
                "");
        return Source.formatAll(source,
                this.generateProcedureDecl(shape),
                encoding,
                this.generateProcedureArguments(shape),
                this.generateAssertSafeInvocationTransfer(),
                doBlock,
                this.generateProcedureReturn(shape));
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
                        CAPSULE_PROFILE_TASK_SUFFIX));
            } else {
                source.add(Source.format(
                        "panini$encapsulated.#0 = new #1#2();",
                        local.getIdentifier(),
                        local.raw(),
                        CAPSULE_PROFILE_TASK_SUFFIX));
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
                    "public final boolean run() {",
                    "    try {",
                    "        panini$capsuleInit();",
                    "        panini$encapsulated.run();",
                    "    } catch (Throwable thrown) {",
                    "        thrown.printStackTrace(System.out);",
                    "        panini$errors.add(thrown);",
                    "    } finally {",
                    "        panini$onTerminate();",
                    "    }",
                    "    try {",
                    "       Panini$System.threads.countDown();",
                    "    } catch (InterruptedException e) {",
                    "        e.printStackTrace();",
                    "    }",
                    "    return true;",
                    "}",
                    "");
        }

        List<String> src = Source.lines(
                "@Override",
                "@SuppressWarnings(\"unchecked\")",
                "public final boolean run() {",
                "    try {",
                "        Panini$Message msg = panini$nextMessage();",
                "        ##",
                "    } catch (Throwable thrown) {",
                "        panini$errors.add(thrown);",
                "    }",
                "    return false;",
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
                "case PANINI$TERMINATE:",
                "    panini$onTerminate();",
                "    return true;",
                "}"));
        return lines;
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
        /*
        return Source.format(
                "assert Panini$Ownership.#0.isSafeTransfer(#1, #2) : #3",
                PaniniProcessor.ownershipCheckMethod,
                "result",
                "panini$getAllState()",
                "\"Procedure return attempted unsafe ownership transfer.\"");
        */
        return "";
    }

    private List<String> generateCapsuleBody()
    {
        List<String> src = new ArrayList<String>();

        src.add(this.generateEncapsulatedDecl());
        src.addAll(this.generateProcedureIDs());
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
