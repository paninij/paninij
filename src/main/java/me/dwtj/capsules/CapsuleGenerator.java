package me.dwtj.capsules;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;


/**
 * Used as a service during compilation to makes an automatically generated
 * file for each class annotated with `@Capsule`.
 */
@SupportedAnnotationTypes("me.dwtj.capsules.Capsule")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CapsuleGenerator extends AbstractProcessor
{
    TypeElement capsuleTypeElement;

    public void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        capsuleTypeElement = getTypeElement("me.dwtj.capsules.Capsule");
    }
    
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment env)
    {
        // TODO: Why just ignore `annotations`? (See jpa-annotation-processor)
        
        Set<? extends Element> annotated = env.getElementsAnnotatedWith(Capsule.class);

        // TODO: Give warnings when the user annotates some element which cannot be a capsule.
        for (Element elem : annotated) {
            if (checkCapsule(elem, env) == true); {
                // TODO: Is this cast a problem?
                processCapsule((TypeElement) elem, env);
            }
        }
        return false;
    }


    /**
     * Processes the given capsule-annotated class in the given processing
     * environment.
     * @param elem
     * @param env
     */
    private void processCapsule(TypeElement elem, RoundEnvironment env)
    {
        makeCapsuleFile(elem, env);
    }


    /**
     * @param elem
     * @return `true` if and only if `elem` is can be processed as a valid
     * capsule.
     */
    private static boolean checkCapsule(Element elem, RoundEnvironment env) {
        // TODO: Also double-check that the element is actually annotated with
        // `@Capsule`.
        // TODO: give warnings/errors when the user annotates an element which cannot be a capsule.
        // TODO: check that the class does not have any inner classes.
        return elem.getKind() == ElementKind.CLASS;
    }
    

    private void makeCapsuleFile(TypeElement cls, RoundEnvironment env)
    {
        Elements utils = processingEnv.getElementUtils();

        Name pkg = utils.getPackageOf(cls).getQualifiedName();
        
        String src = "package {0};\n"
                     + "\n"
                     + "{1}"
                     + "\n"
                     + "\n"
                     + "{2}";

        src = MessageFormat.format(src, pkg, buildCapsuleImports(cls, env),
                                   buildCapsule(cls, env));

        JavaFileObject file = null;
        try {
            String capsuleClass = pkg + "." + cls.getSimpleName() + "Capsule";
            file = processingEnv.getFiler().createSourceFile(capsuleClass);
            file.openWriter().append(src).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String buildCapsuleImports(TypeElement cls, RoundEnvironment env)
    {
        // TODO: Visit the entirety of the definition of `cls`, looking for dependencies that
        // need to be imported. Alternatively, just copy-and-paste all of the imports in the
        // original `.java` file.
        String imports = "import java.util.concurrent.Callable;\n"
                       + "import java.util.concurrent.Future;\n"
                       + "import java.util.concurrent.FutureTask;\n"
                       + "import java.util.concurrent.ConcurrentLinkedQueue;\n";
        return imports;
    }
    
    /**
     * @param origElem A handle to the original class from which a capsule is being
     * built.
     * @param env The environment in which the capsule is being built.
     * @return The source code for the capsule class which has been build to wrap the
     * original class.
     */
    private String buildCapsule(TypeElement orig, RoundEnvironment env)
    {
        note("buildCapsule()");

        Elements utils = processingEnv.getElementUtils();
        Name pkg = utils.getPackageOf(orig).getQualifiedName();
        String qualifiedOrig = pkg + "." + orig.getSimpleName();

        // Note that `MessageFormat.format()` cannot be used here because of the curly-braces.
        return buildCapsuleComment(qualifiedOrig)
             + buildCapsuleDecl(orig, env) + "\n"
             + "{\n"
             + buildCapsuleBody(orig, env)
             + "}\n"
             + "\n";
    }
    
    
    private String buildCapsuleComment(String qualifiedOriginal)
    {
        String comment = "/**\n"
                       + " * This capsule was auto-generated from `{0}`.\n"
                       + " */\n";
        return MessageFormat.format(comment, qualifiedOriginal);
    }
    

    private String buildCapsuleDecl(TypeElement origElem, RoundEnvironment env)
    {
        Name origName = origElem.getSimpleName();
        return MessageFormat.format("public class {0}Capsule extends {1}", origName, origName);
    }


    private String buildCapsuleBody(TypeElement cls, RoundEnvironment env)
    {
        ArrayList<String> decls = new ArrayList<String>();
        decls.add(buildCapsuleFields(cls, env));
        
        for (Element child : cls.getEnclosedElements())
        {
            // For now, ignore everything except for constructors and methods which need to be
            // wrapped with procedures.
            if (child.getKind() == ElementKind.CONSTRUCTOR) {
                // TODO: build constructors!
                decls.add("    // TODO: build constructor\n");
            } else if (needsProceedureWrapper(child)) {
                decls.add(buildProcedure((ExecutableElement) child, env));
            }
        }
        
        return String.join("\n", decls);
    }
    
    
    /**
     * @param cls The class from which this capsule is being built.
     * @return A string of all of the fields which the capsule needs to declare.
     */
    private String buildCapsuleFields(TypeElement cls, RoundEnvironment env) {
        return "    ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<Runnable>();\n";
    }
    

    private String buildProcedure(ExecutableElement method, RoundEnvironment env)
    {
        // Note that `MessageFormat.format()` cannot be used here because of the curly-braces.
        return buildProcedureDecl(method, env) + "\n"
             + "    {\n"
             + buildProcedureBody(method, env)
             + "    }\n"
             + "\n";
    }
    
    
    private String buildProcedureDecl(ExecutableElement method, RoundEnvironment env)
    {
        return MessageFormat.format("    public Future<{0}> {1}Proc({2})",
                                    getBoxedReturnType(method),
                                    method.getSimpleName(),
                                    buildProcedureParameters(method, env));
    }
    
    
    private String buildProcedureParameters(ExecutableElement method, RoundEnvironment env)
    {
        List<String> paramStrings = new ArrayList<String>();
        for (VariableElement param : method.getParameters()) {
            paramStrings.add(buildParamDecl(param, env));
        }
        return String.join(", ", paramStrings);
    }
    
    
    /**
     * @param method This is the method being wrapped by the procedure being build.
     * @param env
     * @return The source code for a procedure which appropriately wraps the given method.
     */
    private String buildProcedureBody(ExecutableElement method, RoundEnvironment env)
    {
        String retType = getBoxedReturnType(method);
        String body;
        body = "        FutureTask<"+ retType + "> f = new FutureTask(\n"
             + "            new Callable<" + retType + ">() {\n"
             + "                public " + retType + " call() {\n"
             + "                    " + buildCallBody(method, env)
             + "                }\n"
             + "            }\n"
             + "        );\n"
             + "        \n"
             + "        queue.add(f);\n"
             + "        return f;\n";
        return body;
    }

   
    private String buildCallBody(ExecutableElement method, RoundEnvironment env)
    {
        String fmt;
        if (CapsuleGenerator.hasVoidReturnType(method)) {
            fmt = "{0}({1}); return null;\n";
        } else {
            fmt = "return {0}({1});\n";
        }
        return MessageFormat.format(fmt, method.getSimpleName(), buildArgsList(method, env));
    }
    
    
    private String buildParamDecl(VariableElement var, RoundEnvironment env)
    {
        return var.asType().toString() + " " + var.toString();
    }
    
    
    private String buildArgsList(ExecutableElement method, RoundEnvironment env)
    {
        List<String> paramStrings = new ArrayList<String>();
        for (VariableElement var : method.getParameters()) {
            paramStrings.add(var.toString());
        }
        return String.join(", ", paramStrings);
    }

   
    
    /* Helper Methods ********************************************************/
    
    /**
     * @param elem
     * @return `true` iff the given `elem` is a method which needs to be 
     * wrapped as a procedure.
     */
    private boolean needsProceedureWrapper(Element elem)
    {
        if (elem.getKind() == ElementKind.METHOD) {
            ExecutableElement method = (ExecutableElement) elem;
            Set<Modifier> modifiers = method.getModifiers();
            if (modifiers.contains(Modifier.PUBLIC)) {
                return true;
            }
        }

        return false;
    }
    
    private static boolean hasVoidReturnType(ExecutableElement exec) {
        return exec.getReturnType().getKind() == TypeKind.VOID;
    }
    
    private static boolean hasPrimitiveReturnType(ExecutableElement exec) {
        return exec.getReturnType().getKind().isPrimitive();
    }
    
    /**
     * Gives a string representation of the executable element's return type. If the return type is
     * a primitive (e.g. int, double, etc.), then the returned type will be that primitive's boxed
     * type (e.g. Integer, Double, etc.).
     * 
     * In the case that the executable element return has no return type (i.e. `void`), then "Void"
     * is returned.
     * 
     * An `IllegalArgumentException` when the `TypeKind` of the return value of the given `exec` is
     * any of the following:
     * 
     *  - NONE
     *  - NULL
     *  - ERROR
     *  - WILDCARD
     *  - PACKAGE
     *  - EXECUTABLE
     *  - OTHER
     *  - UNION
     *  - INTERSECTION
     * 
     * @param exec
     * @return A string of the executable's return type.
     */
    private String getBoxedReturnType(ExecutableElement exec)
    {
        switch (exec.getReturnType().getKind()) {
        case BOOLEAN:
            return "Boolean";
        case BYTE:
            return "Byte";
        case SHORT:
            return "Short";
        case INT:
            return "Integer";
        case LONG:
            return "Long";
        case CHAR:
            return "Character";
        case FLOAT:
            return "Float";
        case DOUBLE:
            return "Double";
        case VOID:
            return "Void";
        case ARRAY:
        case DECLARED:  // A class or interface type.
            return exec.getReturnType().toString();
        case NONE:
        case NULL:
        case ERROR:
        case TYPEVAR:
        case WILDCARD:
        case PACKAGE:
        case EXECUTABLE:
        case OTHER:
        case UNION:         // TODO: What are union and intersection types?
        case INTERSECTION:
        default:
            throw new IllegalArgumentException();
        }
    }

    private TypeElement getTypeElement(String className) {
        return processingEnv.getElementUtils().getTypeElement(className);
    }
    
    private void note(String msg) {
        processingEnv.getMessager().printMessage(Kind.NOTE, msg);
    }
    
    private void warning(String msg) {
        processingEnv.getMessager().printMessage(Kind.WARNING, msg);
    }
    
    private void error(String msg) {
        processingEnv.getMessager().printMessage(Kind.ERROR, msg);
    }
}
