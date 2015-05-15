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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.apt.util.DuckShape;
import org.paninij.apt.util.JavaModelInfo;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;


/**
 * Inspects a given capsule template class an uses information in it to build capsule artifact with
 * the `$Thread` execution profile.
 */
class MakeCapsule$Thread extends MakeCapsule$ExecProfile
{
    private static final String CAPSULE_THREAD_TYPE_SUFFIX = "$Thread";

    static MakeCapsule$Thread make(PaniniProcessor context, TypeElement template)
    {
        MakeCapsule$Thread cap = new MakeCapsule$Thread();
        cap.context = context;
        cap.template = template;
        return cap;
    }

    @Override
    String buildCapsule()
    {
        String src = Source.lines(0, "package #0;",
                                     "",
                                     "#1",
                                     "",
                                     "/**",
                                     " * This capsule was auto-generated from `#2`",
                                     " */",
                                     "#3",
                                     "{",
                                     "#4",
                                     "}");
        return Source.format(src, buildPackage(),
                                  buildImports(),
                                  PaniniModelInfo.qualifiedTemplateName(template),
                                  buildCapsuleDecl(),
                                  buildCapsuleBody());
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


    @Override
    String buildCapsuleBody()
    {
        String src = Source.lines(0, "    /* Procedure IDs */",
                                     "    ##",
                                     "",
                                     "    /* Private Capsule Fields */",
                                     "    #0",
                                     "",
                                     "    /* Capsule procedures */",
                                     "    ##",
                                     "",
                                     "    /* Capsule-specific Panini methods */",
                                     "    ##",
                                     "",
                                     "    ##",
                                     "",
                                     "    ##",
                                     "",
                                     "    ##",
                                     "",
                                     "    ##",
                                     "",
                                     "    ##");
        src = Source.format(src, buildEncapsulatedTemplateInstanceDecl());

        src = Source.formatAligned(src, buildProcedureIDs());
        src = Source.formatAligned(src, buildProcedures());
        src = Source.formatAligned(src, buildCheckRequired());
        src = Source.formatAligned(src, buildWire());
        src = Source.formatAligned(src, buildInitChildren());
        src = Source.formatAligned(src, buildInitState());
        src = Source.formatAligned(src, buildRun());
        src = Source.formatAligned(src, buildMain());

        return src;
    }


    String buildEncapsulatedTemplateInstanceDecl()
    {
        String src = Source.lines(0, "private #0 panini$encapsulated = new #0();");
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
    String buildImports()
    {
        Set<String> imports = getStandardImports();
        for (DuckShape duck : PaniniModelInfo.getDuckShapes(template)) {
            imports.add(duck.getPackage() + "." + duck.encoded + "$Thread");
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
        List<String> lines = Source.linesList(0, "#0",
                                                 "{",
                                                 "    #1$Thread panini$duck = null;",
                                                 "    panini$duck = new #1$Thread(#2);",
                                                 "    panini$push(panini$duck);",
                                                 "    #3",
                                                 "}");

        // Every `args` list starts with a `procID`. If there are any parameter names, then they
        // are all appended to `args`.
        String procID = buildProcedureID(method);
        String args = Source.buildParameterNamesList(method);
        args = args.equals("") ? procID : procID + ", " + args;

        // `void` procedures will not need a return statement, but other procedures will.
        DuckShape duck = new DuckShape(method);
        String maybeReturn = (duck.getSimpleReturnType() != "void") ? "return panini$duck;" : "";

        return Source.formatList(lines, Source.buildExecutableDecl(method),
                                        duck.toString(),
                                        args,
                                        maybeReturn);
    }


    private List<String> buildCheckRequired()
    {
        List<VariableElement> required = PaniniModelInfo.getWiredFieldDecls(context, template);

        if (required.isEmpty()) {
            return new ArrayList<String>();
        }

        List<String> assertions = new ArrayList<String>(required.size());
        for (int idx = 0; idx < required.size(); idx++)
        {
            assertions.add(Source.format("assert(panini$encapsulated.#0 != null);",
                                          required.get(idx).toString()));
        }

        List<String> lines = Source.linesList(0, "@Override",
                                                 "public void panini$checkRequired()",
                                                 "{",
                                                 "    ##",
                                                 "}");
        return Source.formatAlignedList(lines, assertions);
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

        List<String> src = Source.linesList(0, "@Override",
                                               "#0",
                                               "{",
                                               "    ##",
                                               "}");

        src = Source.formatList(src, PaniniModelInfo.buildWireMethodDecl(context, template));
        src = Source.formatAlignedList(src, assignments);

        return src;
    }


    /**
     * Build a method which initializes each of the child capsules and delegates.
     */
    private List<String> buildInitChildren()
    {
        List<VariableElement> children = PaniniModelInfo.getChildFieldDecls(context, template);
        if (children.size() == 0) {
            return new ArrayList<String>();
        }
        
        List<String> lines = new ArrayList<String>();

        // For each of the capsule's children, add a line of code to instantiate that child capsule.
        for (VariableElement child : children)
        {
            String inst = Source.format("panini$encapsulated.#0 = new #1$Thread();",
                                        child.toString(), child.asType().toString());
            lines.add(inst);
        }

        // If the template has a design method, then it will need to be called.
        if (PaniniModelInfo.hasCapsuleDesignDecl(template)) {
            lines.add("panini$encapsulated.design(this);");
        }

        // For each of the capsule's children, add a call to that capsule's `panini$start()` method.
        for (VariableElement child : children)
        {
            lines.add(Source.format("panini$encapsulated.#0.panini$start();", child.toString()));
        }
        
        // Build the method itself.
        List<String> src = Source.linesList(0, "@Override",
                                               "protected void panini$initChildren()",
                                               "{",
                                               "    ##",
                                               "}");

        return Source.formatAlignedList(src, lines);
    }


    List<String> buildInitState()
    {
        // If there is an `init` declaration on the template class, then override the empty `init()`
        // method inherited from the `Capsule$Thread` superclass with a method that delegates to
        // the encapsulated template instance.
        if (PaniniModelInfo.hasInitDeclaration(template))
        {
            return Source.linesList(0, "@Override",
                                       "protected void panini$initState() {",
                                       "    panini$encapsulated.init();",
                                       "}");
        }
        else
        {
            return new ArrayList<String>();  // Do not override superclass with anything.
        }
    }


    List<String> buildRun()
    {
        if (PaniniModelInfo.isActive(template))
        {
            return Source.linesList(0,
                "@Override",
                "public void run()",
                "{",
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
            List<String> src = Source.linesList(0,
                "@Override",
                "public void run()",
                "{",
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
                "    catch (Exception ex) { /* do nothing for now */ }",
                "}"
            );

            return Source.formatAlignedList(src, buildRunSwitch());
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

        lines.addAll(Source.linesList(0, "case PANINI$SHUTDOWN:",
                                         "    if (panini$isEmpty() == false) {",
                                         "        panini$push(msg);",
                                         "    } else {",
                                         "        terminate = true;",
                                         "    }",
                                         "    break;",
                                         "",
                                         "case PANINI$EXIT:",
                                         "    terminate = true;",
                                         "    break;"));
        lines.add("    }");

        return lines;
    }


    /**
     * Assumes that `method` is a procedure method on a valid capsule template.
     */
    List<String> buildRunSwitchCase(ExecutableElement method)
    {
        // `duck` will need to be resolved if and only if `method` has a return value.
        if (JavaModelInfo.hasVoidReturnType(method))
        {
            // Simply call the template isntance's method with the args encapsulated in the duck.
            List<String> src = Source.linesList(0, "case #0:",
                                                   "    #1;",
                                                   "    break;");

            return Source.formatList(src, buildProcedureID(method),
                                          buildEncapsulatedMethodCall(method));
        }
        else
        {
            // Call the template instance's method and resolve the duck using the result.
            List<String> src = Source.linesList(0, "case #0:",
                                                   "    ((Panini$Future<#1>) msg).panini$resolve(#2);",
                                                   "    break;");

            return Source.formatList(src, buildProcedureID(method),
                                          method.getReturnType().toString(),
                                          buildEncapsulatedMethodCall(method));
        }
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
    String buildEncapsulatedMethodCall(ExecutableElement method)
    {
        List<String> args = new ArrayList<String>();
        String duckType = DuckShape.encode(method);

        // Generate the list of types defined on the `method`. The `null` value is used to
        // represent a parameter type whenever that type is primitive.
        List<String> paramTypes = new ArrayList<String>();
        for (VariableElement varElem : method.getParameters())
        {
            TypeMirror paramType = varElem.asType();
            paramTypes.add(JavaModelInfo.isPrimitive(paramType) ? null : paramType.toString());
        }

        // Extract each argument held in the duck. For each of these extractions, one type cast is
        // used to convert the `Panini$Message` to a concrete duck type. If the duck is storing
        // an object in an `Object` box, then another type cast is used to convert that argument to
        // its original type.
        for (int i = 0; i < paramTypes.size(); i++)
        {
            String paramType = paramTypes.get(i);
            args.add(Source.format("#0((#1$Thread) msg).panini$arg#2",
                                   paramType == null ? "" : "(" + paramType + ") ",
                                   duckType,
                                   i));
        }

        return Source.format("panini$encapsulated.#0(#1)", method.getSimpleName(),
                                                           String.join(", ", args));
    }


    private List<String> buildMain()
    {
        // A `Capsule$Thread` should have a main() method if and only if it is a "root" capsule.
        if (PaniniModelInfo.isRootCapsule(context, template))
        {
             List<String> src = Source.linesList(0, "public static void main(String[] args)",
                                                    "{",
                                                    "    #0 root = new #0();",
                                                    "    root.run();",
                                                    "}");
             return Source.formatList(src, buildCapsuleName());
        }
        else
        {
            return new ArrayList<String>();
        }
    }


    @Override
    Set<String> getStandardImports() {
        Set<String> imports = new HashSet<String>();
        imports.add("org.paninij.runtime.Capsule$Thread");
        imports.add("org.paninij.runtime.Panini$Message");
        imports.add("org.paninij.runtime.Panini$Future");
        return imports;
    }
}
