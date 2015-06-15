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

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.paninij.apt.util.JavaModelInfo;
import org.paninij.apt.util.MessageShape;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;
import org.paninij.model.Capsule;
import org.paninij.model.Procedure;
import org.paninij.model.ProcedureElement;
import org.paninij.model.Type;
import org.paninij.model.Variable;


/**
 * Inspects a given capsule template class an uses information in it to build capsule artifact with
 * the `$Thread` execution profile.
 */
class MakeCapsule$Thread extends MakeCapsule$ExecProfile
{
    private static final String CAPSULE_THREAD_TYPE_SUFFIX = "$Thread";

    static MakeCapsule$Thread make(PaniniProcessor context, TypeElement template, Capsule capsule)
    {
        MakeCapsule$Thread cap = new MakeCapsule$Thread();
        cap.context = context;
        cap.template = template;
        cap.capsule = capsule;
        return cap;
    }

    @Override
    String buildCapsule()
    {
        String src = Source.cat("package #0;",
                                "",
                                "##",
                                "",
                                "/**",
                                " * This capsule was auto-generated from `#1`",
                                " */",
                                "@SuppressWarnings(\"all\")",  // Suppress unused imports.
                                "#2",
                                "{",
                                "    ##",
                                "}");

        src = Source.format(src, buildPackage(),
                                 PaniniModelInfo.qualifiedTemplateName(template),
                                 buildCapsuleDecl());
        src = Source.formatAligned(src, buildImports());
        src = Source.formatAligned(src, buildCapsuleBody());

        return src;
    }


    @Override
    String buildCapsuleName() {
        return PaniniModelInfo.simpleCapsuleName(template) + CAPSULE_THREAD_TYPE_SUFFIX;
    }


    @Override
    String buildQualifiedCapsuleName() {
        return PaniniModelInfo.qualifiedCapsuleName(template) + CAPSULE_THREAD_TYPE_SUFFIX;
    }


    @Override
    String buildCapsuleDecl() {
        return Source.format("public class #0 extends Capsule$Thread implements #1",
                             buildCapsuleName(), PaniniModelInfo.simpleCapsuleName(template));
    }


    // TODO: How can this be done without the `@SuppressWarnings`?
    @SuppressWarnings("unchecked")
    @Override
    List<String> buildCapsuleBody()
    {
        List<String> src = new ArrayList<String>();
        src.add(buildEncapsulatedTemplateInstanceDecl());

        // TODO: How can this be done without the `@SuppressWarnings`?
        @SuppressWarnings("rawtypes")
        List[] xs = {
            buildProcedureIDs(),
            buildProcedures(),
            buildCheckRequired(),
            buildWire(),
            buildInitChildren(),
            buildInitState(),
            buildGetAllState(),
            buildRun(),
            buildMain()
        };

        for (List<String> x : xs) {
            src.addAll(x);
            src.add("");
        }

        return src;
    }


    String buildEncapsulatedTemplateInstanceDecl()
    {
        String src = "private #0 panini$encapsulated = new #0();";
        return Source.format(src, PaniniModelInfo.simpleTemplateName(template));
    }


    List<String> buildProcedureIDs()
    {
        ArrayList<String> decls = new ArrayList<String>();
        int currID = 0;
        for (Element child : template.getEnclosedElements())
        {
            if (PaniniModelInfo.isProcedure(child))
            {
                String decl = Source.format("public static final int #0 = #1;",
                                            buildProcedureID((ExecutableElement)child),
                                            currID);
                decls.add(decl);
                currID++;
            }
        }

        return decls;
    }

    String buildProcedureID(ExecutableElement method)
    {
        String base = "panini$proc$";
        String name = method.getSimpleName().toString();
        List<String> params = new ArrayList<String>();
        for (VariableElement param : method.getParameters()) {
            params.add(parseType(param.asType()));
        }
        String paramStrings = params.size() > 0 ? "$" + String.join("$", params) : "";

        return base + name + paramStrings;
    }


    // TODO: This needs a much better name.
    String parseType(TypeMirror type) {
        String src = type.toString().replaceAll("\\.", "_");
        src = src.replaceAll("\\[", "").replaceAll("\\]", "Array");
        return src;
    }


    @Override
    List<String> buildImports()
    {
        Set<String> imports = getStandardImports();

        for (Procedure p : this.capsule.getProcedures()) {
            MessageShape shape = new MessageShape(p);
            imports.add(shape.getPackage() + "." +shape.encoded);
        }

        return Source.buildCollectedImportDecls(template, imports);
    }
    
    
    List<String> buildProcedures()
    {
        ArrayList<String> lines = new ArrayList<String>();

        for (Element child : template.getEnclosedElements())
        {
            // TODO: For now, ignore everything except for methods which need to be wrapped
            // procedures. In the future, other enclosed elements may need to be treated specially
            // while building the capsule body.
            if (PaniniModelInfo.isProcedure(child)) {
                lines.addAll(buildProcedure((ExecutableElement) child));
            }
        }

        return lines;
    }


    List<String> buildProcedure(ExecutableElement method)
    {
        Procedure p = new ProcedureElement(method);

        MessageShape shape = new MessageShape(p);

        // TODO all shapes defined, now delegate!
        switch (shape.behavior) {
        case BLOCKED_FUTURE:
            return this.buildBlockedFutureProcedure(method);
        case BLOCKED_PREMADE:
            break;
        case ERROR:
            break;
        case UNBLOCKED_DUCK:
            return this.buildDuckProcedure(method);
        case UNBLOCKED_FUTURE:
            return this.buildFutureProcedure(method);
        case UNBLOCKED_PREMADE:
            break;
        case UNBLOCKED_SIMPLE:
            return this.buildSimpleProcedure(method);
        default:
            break;
        }
        return null;
    }

    List<String> buildDuckProcedure(ExecutableElement method) {
        List<String> lines = Source.lines("#0",
                "{",
                "    #1 panini$message = null;",
                "    panini$message = new #1(#2);",
                "    Capsule$Thread caller = Panini$System.self.get();",
                "    #3;",
                "    panini$push(panini$message);",
                "    return panini$message;",
                "}");

        // Every `args` list starts with a `procID`. If there are any parameter names, then they
        // are all appended to `args`.
        String procID = buildProcedureID(method);
        String args = Source.buildParameterNamesList(method);
        args = args.equals("") ? procID : procID + ", " + args;

        // `void` procedures will not need a return statement, but other procedures will.
        MessageShape shape = new MessageShape(new ProcedureElement(method));

        return Source.formatAll(lines,
                Source.buildExecutableDecl(method),
                shape.encoded,
                args,
                buildAssertSafeInvocationTransfer());
    }

    List<String> buildBlockedFutureProcedure(ExecutableElement method) {
        Procedure p = new ProcedureElement(method);
        MessageShape shape = new MessageShape(new ProcedureElement(method));

        List<String> lines = Source.lines(
                "#0",
                "{",
                "    #1 panini$message = null;",
                "    panini$message = new #1(#2);",
                "    Capsule$Thread caller = Panini$System.self.get();",
                "    #3;",
                "    panini$push(panini$message);",
                "    #4panini$message.get();",
                "}");
        String procID = buildProcedureID(method);

        List<String> argDecls = new ArrayList<String>();
        List<String> argNames = new ArrayList<String>();


        argNames.add(procID);
        for (Variable v : p.getParameters()) {
            argDecls.add(v.toString());
            argNames.add(v.getIdentifier());
        }

        String argDeclString = String.join(", ", argDecls);
        String argNameString = String.join(", ", argNames);

        String declaration = Source.format("#0 #1 #2(#3)",
                Source.buildModifiersList(method),
                shape.realReturn,
                p.getName(),
                argDeclString);

        List<String> thrown = new ArrayList<String>();
        for (TypeMirror t : method.getThrownTypes()) {
            thrown.add(t.toString());
        }

        declaration += (thrown.isEmpty()) ? "" : " throws " + String.join(", ", thrown);
        String ret = shape.returnType.isVoid() ? "" : "return ";
        return Source.formatAll(lines,
                declaration,
                shape.encoded,
                argNameString,
                buildAssertSafeInvocationTransfer(),
                ret);
    }

    List<String> buildFutureProcedure(ExecutableElement method) {
        Procedure p = new ProcedureElement(method);
        MessageShape shape = new MessageShape(new ProcedureElement(method));

        List<String> lines = Source.lines(
                "#0",
                "{",
                "    #1 panini$message = null;",
                "    panini$message = new #1(#2);",
                "    Capsule$Thread caller = Panini$System.self.get();",
                "    #3;",
                "    panini$push(panini$message);",
                "    return panini$message;",
                "}");
        String procID = buildProcedureID(method);

        List<String> argDecls = new ArrayList<String>();
        List<String> argNames = new ArrayList<String>();


        argNames.add(procID);
        for (Variable v : p.getParameters()) {
            argDecls.add(v.toString());
            argNames.add(v.getIdentifier());
        }

        String argDeclString = String.join(", ", argDecls);
        String argNameString = String.join(", ", argNames);

        String declaration = Source.format("#0 #1 #2(#3)",
                Source.buildModifiersList(method),
                shape.realReturn,
                p.getName(),
                argDeclString);

        List<String> thrown = new ArrayList<String>();
        for (TypeMirror t : method.getThrownTypes()) {
            thrown.add(t.toString());
        }

        declaration += (thrown.isEmpty()) ? "" : " throws " + String.join(", ", thrown);

        return Source.formatAll(lines,
                declaration,
                shape.encoded,
                argNameString,
                buildAssertSafeInvocationTransfer());
    }

    List<String> buildBlockedProcedure(ExecutableElement method) {
        Procedure p = new ProcedureElement(method);
        MessageShape shape = new MessageShape(new ProcedureElement(method));

        List<String> lines = Source.lines(
                "#0",
                "{",
                "    #1 panini$message = null;",
                "    panini$message = new #1(#2);",
                "    Capsule$Thread caller = Panini$System.self.get();",
                "    #3;",
                "    panini$push(panini$message);",
                "    return panini$message.get();",
                "}");
        String procID = buildProcedureID(method);

        List<String> argDecls = new ArrayList<String>();
        List<String> argNames = new ArrayList<String>();


        argNames.add(procID);
        for (Variable v : p.getParameters()) {
            argDecls.add(v.toString());
            argNames.add(v.getIdentifier());
        }

        String argDeclString = String.join(", ", argDecls);
        String argNameString = String.join(", ", argNames);

        String declaration = Source.format("#0 #1 #2(#3)",
                Source.buildModifiersList(method),
                shape.realReturn,
                p.getName(),
                argDeclString);

        List<String> thrown = new ArrayList<String>();
        for (TypeMirror t : method.getThrownTypes()) {
            thrown.add(t.toString());
        }

        declaration += (thrown.isEmpty()) ? "" : " throws " + String.join(", ", thrown);

        return Source.formatAll(lines,
                declaration,
                shape.encoded,
                argNameString,
                buildAssertSafeInvocationTransfer());
    }

    List<String> buildSimpleProcedure(ExecutableElement method) {
        Procedure p = new ProcedureElement(method);
        MessageShape shape = new MessageShape(new ProcedureElement(method));

        List<String> lines = Source.lines(
                "#0",
                "{",
                "    #1 panini$message = null;",
                "    panini$message = new #1(#2);",
                "    Capsule$Thread caller = Panini$System.self.get();",
                "    #3;",
                "    panini$push(panini$message);",
                "}");
        String procID = buildProcedureID(method);

        List<String> argDecls = new ArrayList<String>();
        List<String> argNames = new ArrayList<String>();


        argNames.add(procID);
        for (Variable v : p.getParameters()) {
            argDecls.add(v.toString());
            argNames.add(v.getIdentifier());
        }

        String argDeclString = String.join(", ", argDecls);
        String argNameString = String.join(", ", argNames);

        String declaration = Source.format("#0 #1 #2(#3)",
                Source.buildModifiersList(method),
                shape.realReturn,
                p.getName(),
                argDeclString);

        List<String> thrown = new ArrayList<String>();
        for (TypeMirror t : method.getThrownTypes()) {
            thrown.add(t.toString());
        }

        declaration += (thrown.isEmpty()) ? "" : " throws " + String.join(", ", thrown);

        return Source.formatAll(lines,
                declaration,
                shape.encoded,
                argNameString,
                buildAssertSafeInvocationTransfer());
    }

    
    public static String buildAssertSafeInvocationTransfer() {
        return "assert Capsule$Thread.panini$isSafeTransfer(panini$message, caller.panini$getAllState()) : \"Procedure invocation performed unsafe ownership transfer.\"";
    }


    private List<String> buildCheckRequired()
    {
        // Get the fields which must be non-null, i.e. all wired fields and all arrays of children.
        List<VariableElement> required = PaniniModelInfo.getWiredFieldDecls(context, template);
        for (VariableElement child: PaniniModelInfo.getChildFieldDecls(context, template)) {
            if (child.asType().getKind() == TypeKind.ARRAY) {
                required.add(child);
            }
        }

        if (required.isEmpty()) {
            return new ArrayList<String>();
        }

        List<String> assertions = new ArrayList<String>(required.size());
        for (int idx = 0; idx < required.size(); idx++)
        {
            assertions.add(Source.format("assert(panini$encapsulated.#0 != null);",
                                          required.get(idx).toString()));
        }

        List<String> lines = Source.lines("@Override",
                                          "public void panini$checkRequired()",
                                          "{",
                                          "    ##",
                                          "}");
        return Source.formatAlignedFirst(lines, assertions);
    }
    

    private List<String> buildWire()
    {
        if (PaniniModelInfo.hasWiredFieldDecls(context, template) == false) {
            return new ArrayList<String>();
        }

        // Assign each of the `wire()` method's arguments into the corresponding field of the
        // encapsulated template instance.
        List<String> assignments = new ArrayList<String>();
        for (VariableElement req : PaniniModelInfo.getWiredFieldDecls(context, template)) {
            assignments.add(Source.format("panini$encapsulated.#0 = #0;", req.toString()));
        }

        List<String> src = Source.lines("@Override",
                                        "#0",
                                        "{",
                                        "    ##",
                                        "}");

        src = Source.formatAll(src, PaniniModelInfo.buildWireMethodDecl(context, template));
        src = Source.formatAlignedFirst(src, assignments);

        return src;
    }


    /**
     * Build a method which initializes each of the child capsules and delegates.
     */
    private List<String> buildInitChildren()
    {
        List<Variable> children = this.capsule.getChildren();

        if (children.size() == 0) {
            return new ArrayList<String>();
        }

        List<String> lines = new ArrayList<String>();

        // For each of the capsule's children, add a line of code to instantiate that child capsule.
        for (Variable child : children)
        {
            if (child.isArray()) {
                ArrayType t = (ArrayType) child.getMirror();
                Type comp = new Type(t.getComponentType());

                List<String> src = Source.lines(
                        "for (int i = 0; i < panini$encapsulated.#0.length; i++) {",
                        "    panini$encapsulated.#0[i] = new #1$Thread();",
                        "}");
                lines.addAll(Source.formatAll(src, child.getIdentifier(), comp.getMirror().toString()));
            } else {
                lines.add(Source.format(
                        "panini$encapsulated.#0 = new #1$Thread();",
                        child.getIdentifier(),
                        child.getMirror().toString()));
            }
        }

        // If the template has a design method, then it will need to be called.
        if (PaniniModelInfo.hasCapsuleDesignDecl(template)) {
            lines.add("panini$encapsulated.design(this);");
        }

        for (Variable child : children)
        {
            if (child.isArray()) {
                List<String> src = Source.lines(
                        "for (int i = 0; i < panini$encapsulated.#0.length; i++) {",
                        "    panini$encapsulated.#0[i].panini$start();",
                        "}");
                lines.addAll(Source.formatAll(src, child.getIdentifier()));
            } else {
                lines.add(Source.format(
                        "panini$encapsulated.#0.panini$start();",
                        child.getIdentifier()));
            }
        }


        // Build the method itself.
        List<String> src = Source.lines("@Override",
                                        "protected void panini$initChildren()",
                                        "{",
                                        "    ##",
                                        "}");

        return Source.formatAlignedFirst(src, lines);
    }


    List<String> buildInitState()
    {
        // If there is an `init` declaration on the template class, then override the empty `init()`
        // method inherited from the `Capsule$Thread` superclass with a method that delegates to
        // the encapsulated template instance.
        if (PaniniModelInfo.hasInitDeclaration(template))
        {
            return Source.lines("@Override",
                                "protected void panini$initState() {",
                                "    panini$encapsulated.init();",
                                "}");
        }
        else
        {
            return new ArrayList<String>();  // Do not override superclass with anything.
        }
    }
    
    
    List<String> buildGetAllState()
    {
        List<String> states = new ArrayList<String>();
        for (VariableElement state_elem : PaniniModelInfo.getStateFieldDecls(context, template))
        {
            TypeKind state_kind = state_elem.asType().getKind();
            if (state_kind == TypeKind.ARRAY || state_kind == TypeKind.DECLARED) {
                states.add("panini$encapsulated." + state_elem.toString());
            }
        }

        List<String> src = Source.lines("public Object panini$getAllState()",
                                        "{",
                                        "    Object[] state = {#0};",
                                        "    return state;",
                                        "}");
        return Source.formatAll(src, String.join(", ", states));
    }


    List<String> buildRun()
    {
        if (PaniniModelInfo.isActive(template))
        {
            return Source.lines(
                "@Override",
                "public void run()",
                "{",
                "    Panini$System.self.set(this);",
                "    try",
                "    {",
                "        panini$checkRequired();",
                "        panini$initChildren();",
                "        panini$initState();",
                "        panini$encapsulated.run();",
                "    } finally {",
                "        // TODO?",
                "    }",
                "}"
            );
        }
        else
        {
            List<String> src = Source.lines(
                "@SuppressWarnings(\"all\")",  // To suppress "unsafe" casts in message unpacking.
                "@Override",
                "public void run()",
                "{",
                "    Panini$System.self.set(this);",
                "    try",
                "    {",
                "        panini$checkRequired();",
                "        panini$initChildren();",
                "        panini$initState();",
                "",
                "        boolean terminate = false;",
                "        while (!terminate)",
                "        {",
                "            Panini$Message msg = panini$nextMessage();",
                "            ##",
                "        }",
                "    }",
                "    catch (Exception ex) {",
                "        /* do nothing for now */",
                "    }",
                "}"
            );

            return Source.formatAlignedFirst(src, buildRunSwitch());
        }
    }


    List<String> buildRunSwitch()
    {
        List<String> lines = new ArrayList<String>();
        lines.add("switch(msg.panini$msgID()) {");

        // Add a case statement for each procedure wrapper.
        for (Element elem : template.getEnclosedElements())
        {
            if (PaniniModelInfo.isProcedure(elem)) {
                lines.addAll(buildRunSwitchCase((ExecutableElement) elem));
            }
        }

        lines.addAll(Source.lines("case PANINI$SHUTDOWN:",
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


    /**
     * Assumes that `method` is a procedure method on a valid capsule template.
     */
    List<String> buildRunSwitchCase(ExecutableElement method) {
        Procedure p = new ProcedureElement(method);
        MessageShape shape = new MessageShape(p);


        // `duck` will need to be resolved if and only if `method` has a return value.
        if (shape.category == MessageShape.Category.SIMPLE)
        {
            // Simply call the template isntance's method with the args encapsulated in the duck.
            List<String> src = Source.lines("case #0:",
                                            "    #1;",
                                            "    break;");

            return Source.formatAll(src, buildProcedureID(method),
                                         buildEncapsulatedMethodCall(shape));
        }
        else
        {
            Type r = p.getReturnType();
            if (r.isVoid()) {
             // Call the template instance's method and resolve the duck using null.
                List<String> src = Source.lines("case #0:",
                                                "    #1;",
                                                "    ((Panini$Future<#2>) msg).panini$resolve(null);",
                                                "    break;");
                return Source.formatAll(src,
                        buildProcedureID(method),
                        buildEncapsulatedMethodCall(shape),
                        p.getReturnType().wrapped());
            } else {
                // Call the template instance's method and resolve the duck using the result.
                List<String> src = Source.lines("case #0: {",
                                                "    #1 result = #2;",
                                                "    #3;",
                                                "    ((Panini$Future<#1>) msg).panini$resolve(result);",
                                                "    break;",
                                                "}");
                return Source.formatAll(src,
                        buildProcedureID(method),
                        p.getReturnType().wrapped(),
                        buildEncapsulatedMethodCall(shape),
                        buildAssertSafeResultTransfer());
            }
        }
    }
    

    public static String buildAssertSafeResultTransfer() {
        return "assert Capsule$Thread.panini$isSafeTransfer(result, panini$encapsulated) : \"Procedure return attempted unsafe ownership transfer.\"";
    }


    /**
     * Builds a string used to call the given `method` on the encapsulated template instance. This
     * is meant to be used in the context of the capsule's `run()` method.
     *
     * Assumes that the duck being unpacked can be cast to the `DuckShape` type which is used to
     * handle the given `method`.
     *
     * @param duck The name of the duck variable from which arguments will be unpacked.
     */
    String buildEncapsulatedMethodCall(MessageShape shape)
    {
        List<String> args = new ArrayList<String>();

        // Generate the list of types defined on the `method`. The `null` value is used to
        // represent a parameter type whenever that type is primitive.
        List<String> paramTypes = new ArrayList<String>();
        for (Variable v : shape.procedure.getParameters())
        {
            TypeMirror paramType = v.getMirror();
            paramTypes.add(JavaModelInfo.isPrimitive(paramType) ? null : paramType.toString());
        }

        // Extract each argument held in the duck. For each of these extractions, one type cast is
        // used to convert the `Panini$Message` to a concrete duck type. If the duck is storing
        // an object in an `Object` box, then another type cast is used to convert that argument to
        // its original type.
        for (int i = 0; i < paramTypes.size(); i++)
        {
            String paramType = paramTypes.get(i);
            args.add(Source.format("#0((#1) msg).panini$arg#2",
                                   paramType == null ? "" : "(" + paramType + ") ",
                                   shape.encoded,
                                   i));
        }

        return Source.format("panini$encapsulated.#0(#1)", shape.procedure.getName(),
                                                           String.join(", ", args));
    }


    List<String> buildMain()
    {
        // A `Capsule$Thread` should have a main() method if and only if it is a "root" capsule.
        if (PaniniModelInfo.isRootCapsule(context, template))
        {
             List<String> src = Source.lines("public static void main(String[] args)",
                                             "{",
                                             "    #0 root = new #0();",
                                             "    root.run();",
                                             "}");
             return Source.formatAll(src, buildCapsuleName());
        }
        else
        {
            return new ArrayList<String>();
        }
    }


    @Override
    Set<String> getStandardImports() {
        Set<String> imports = new HashSet<String>();
        imports.add("java.util.concurrent.Future");
        imports.add("org.paninij.runtime.Capsule$Thread");
        imports.add("org.paninij.runtime.Panini$Message");
        imports.add("org.paninij.runtime.Panini$Future");
        imports.add("org.paninij.runtime.Panini$System");
        return imports;
    }
}
