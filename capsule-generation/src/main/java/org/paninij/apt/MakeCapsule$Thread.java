package org.paninij.apt;

import java.util.ArrayList;
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

    static MakeCapsule$Thread make(PaniniPress context, TypeElement template)
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
                                  buildCapsuleImports(),
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
        // TODO: Remove trailing whitespace from format string after GitHub Issue #24 is resolved.
        return Source.format("public class #0 extends Capsule$Thread implements #1 ",
                             buildCapsuleName(),
                             PaniniModelInfo.simpleCapsuleName(template));
    }


    @Override
    String buildCapsuleBody()
    {
        String src = Source.lines(0, "    /* Capsule fields: */",
                                     "#0",
                                     "",
                                     "    /* Capsule procedures: */",
                                     "#1",
                                     "",
                                     "    /* Capsule-specific Panini methods: */",
                                     "#2",
                                     "#3",
                                     "#4",
                                     "#5",
                                     "#6");
        return Source.format(src, buildCapsuleFields(),
                                  buildProcedures(),
                                  buildCheckRequired(),
                                  buildWire(),
                                  buildInitChildren(),
                                  buildInitState(),
                                  buildRun());
    }

    @Override
    String buildCapsuleFields()
    {
        String src = Source.lines(0, "#0",
                                     "",
                                     "#1");
        return Source.format(src, buildPaniniEncapsulatedDecl(), buildProcedureIDs());
    }

    String buildPaniniEncapsulatedDecl()
    {
        String src = Source.lines(1, "private #0 panini$encapsulated;");
        return Source.format(src, PaniniModelInfo.simpleTemplateName(template));
    }

    String buildProcedureIDs() {
        ArrayList<String> decls = new ArrayList<String>();
        String src = Source.lines(1, "#0");
        int currID = 0;
        for (Element child : template.getEnclosedElements())
        {
            if (PaniniModelInfo.needsProcedureWrapper(child))
            {
                String decl = Source.format("public static final int #0 = #1;",
                                            buildProcedureID((ExecutableElement)child),
                                            currID);
                decls.add(decl);
                currID++;
            }
        }
        return Source.format(src, String.join("\n    ", decls));
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

    String buildProcedures()
    {
        ArrayList<String> decls = new ArrayList<String>();
        decls.add("");

        for (Element child : template.getEnclosedElements())
        {
            // TODO: For now, ignore everything except for methods which need to be wrapped
            // procedures. In the future, other enclosed elements may need to be treated specially
            // while building the capsule body.
            if (PaniniModelInfo.needsProcedureWrapper(child)) {
                decls.add(buildProcedure((ExecutableElement) child));
            }
        }

        return String.join("\n", decls);
    }


    @Override
    String buildProcedure(ExecutableElement method)
    {
        String src = Source.lines(0, "    #0",
                                     "    {",
                                     "#1",
                                     "    }");

        return Source.format(src, Source.buildExecutableDecl(method),
                                  buildProcedureBody(method));
    }

    String buildProcedureBody(ExecutableElement method)
    {

        DuckShape duck = new DuckShape(method);
        String possibleReturn = "";

        // Append the list of parameter names to `args` if there are any.
        String procID = buildProcedureID(method);
        String args = Source.buildParameterNamesList(method);
        args = args.equals("") ? procID : procID + ", " + args;

        if(duck.getSimpleReturnType() != "void")
        {
            possibleReturn = "return panini$duck;";
        }

        String fmt = Source.lines(2, "#0$Thread panini$duck = null;",
                                     "panini$duck = new #0$Thread(#1);",
                                     "panini$push(panini$duck);",
                                     "#2");
        return Source.format(fmt, duck.toString(), args, possibleReturn);
    }

    private String buildCheckRequired()
    {
        List<VariableElement> required = PaniniModelInfo.getCapsuleRequirements(context, template);
        String[] assertions = new String[required.size()];
        for (int idx = 0; idx < required.size(); idx++) {
            assertions[idx] = Source.format("    assert(panini$encapsulated.#0 != null);", required.get(idx).toString());
        }

        String src = Source.lines(1, "@Override",
                                     "public void panini$checkRequired()",
                                     "{",
                                     "    #0",
                                     "}");
        return Source.format(src, String.join("\n", assertions));
    }

    String buildWire()
    {
        if (PaniniModelInfo.hasCapsuleDesignDecl(template) == false) {
            return "";
        }
        
        // Pass-through each of the arguments to `wire()` to the encapsulated template's `design()`,
        // except `this` should always be passed as the first parameter.
        List<String> args = new ArrayList<String>();
        args.add("this");
        for (VariableElement varElem : PaniniModelInfo.getCapsuleRequirements(context, template)) {
            args.add(varElem.toString());
        }
 
        String src = Source.lines(1, "@Override",
                                     "#0",
                                     "{",
                                     "    panini$encapsulated.design(#1);",
                                     "}");

        return Source.format(src, PaniniModelInfo.buildCapsuleWireMethodDecl(template),
                                  String.join(", ", args));
    }

    /**
     * Build a method which initializes each of the child capsules and delegates.
     */
    String buildInitChildren()
    {
        // TODO: Everything!
        String src = Source.lines(1, "protected void panini$initChildren()",
                                     "{",
                                     "    // TODO: Everything!",
                                     "",
                                     "}");

        List<VariableElement> children = PaniniModelInfo.getCapsuleChildren(context, template);

        return src;
    }

    String buildInitState()
    {
        // If there is an `init` declaration on the template class, then override the empty `init()`
        // method inherited from the `Capsule$Thread` superclass with a method that delegates to
        // the encapsulated template instance.
        if (PaniniModelInfo.hasInitDeclaration(template))
        {
            return Source.lines(1, "@Override",
                                   "protected void panini$initState() {",
                                   "    panini$encapsulated.init();",
                                   "}");
        }
        else
        {
            return "";  // Do not override superclass with anything.
        }
    }

    String buildRun()
    {
        if (PaniniModelInfo.isActive(template))
        {
            return Source.lines(1, "public void run()",
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
                                   "}");
        }
        else
        {
            String src = Source.lines(1, "public void run()",
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
                                         "#0",
                                         "        }",
                                         "    }",
                                         "    catch (Exception ex) { /* do nothing for now */ }",
                                         "}");
            return Source.format(src, buildRunSwitch());
        }
    }

    String buildRunSwitch()
    {
        // Add a case statement for each procedure wrapper.
        List<String> lines = new ArrayList<String>();
        lines.add("switch(msg.panini$msgID()) {");

        for (Element elem : template.getEnclosedElements())
        {
            if (PaniniModelInfo.needsProcedureWrapper(elem)) {
                // TODO: Fix this ugly hack. (Used to make alignment work).
                lines.add("\n" + buildRunSwitchCase((ExecutableElement) elem));
            }
        }

        // TODO: Fix this ugly alignment hack.
        lines.add("\n" + Source.lines(4, "case PANINI$SHUTDOWN:",
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

        String tabs = "            ";  // Three "tabs" of 4-spaces.
        return tabs + String.join("\n" + tabs, lines);
    }

    /**
     * Assumes that `method` is a procedure method on a valid capsule template.
     */
    String buildRunSwitchCase(ExecutableElement method)
    {
        // `duck` will need to be resolved if and only if `method` has a return value.
        if (JavaModelInfo.hasVoidReturnType(method))
        {
            // Simply call the template isntance's method with the args encapsulated in the duck.
            String src = Source.lines(4, "case #0:",
                                         "    #1;",
                                         "    break;");
            return Source.format(src, buildProcedureID(method),
                                      buildEncapsulatedMethodCall(method));
        }
        else
        {
            // Call the template instance's method and resolve the duck using the result.
            String src = Source.lines(4, "case #0:",
                                         "    ((Panini$Future<#1>) msg).panini$resolve(#2);",
                                         "    break;");
            return Source.format(src, buildProcedureID(method),
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
            // TODO: Remove trailing whitespace from `fmt` once GitHub Issue #24 has been resolved.
            args.add(Source.format("#0((#1$Thread) msg).panini$arg#2 ",
                                   paramType == null ? "" : "(" + paramType + ") ",
                                   duckType,
                                   i));
        }

        return Source.format("panini$encapsulated.#0(#1)", method.getSimpleName(),
                                                           String.join(", ", args));
    }

    @Override
    Set<String> getStandardImports() {
        Set<String> imports = new HashSet<String>();
        imports.add("org.paninij.runtime.Capsule$Thread");
        imports.add("org.paninij.runtime.ducks.*");
        imports.add("org.paninij.runtime.Panini$Message");
        imports.add("org.paninij.runtime.Panini$Future");
        return imports;
    }
}
