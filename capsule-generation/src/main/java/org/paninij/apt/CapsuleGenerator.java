package org.paninij.apt;

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

import org.paninij.lang.Capsule;
import org.paninij.util.Source;


/**
 * Used as a service during compilation to makes an automatically generated
 * file for each class annotated with `@Capsule`.
 */
@SupportedAnnotationTypes("org.paninij.lang.Capsule")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CapsuleGenerator extends AbstractProcessor
{
    TypeElement capsuleTypeElement;

    public void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        capsuleTypeElement = getTypeElement("org.paninij.lang.Capsule");
    }
    
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment env)
    {
        // TODO: Why just ignore `annotations`? (See jpa-annotation-processor)
        
        Set<? extends Element> annotated = env.getElementsAnnotatedWith(Capsule.class);

        // TODO: Give warnings when the user annotates some element which cannot be a capsule.
        for (Element elem : annotated) {
            if (checkCapsule(elem, env) == true) {
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
    

    private void makeCapsuleFile(TypeElement template, RoundEnvironment env)
    {
        String pkg = getFullyQualifiedPackageName(template);
        String src = Source.lines(0, "package #0;",
                                     "",
                                     "#1",
                                     "",
                                     "",
                                     "#2");

        src = Source.format(src, pkg,
                                       buildCapsuleImports(template, env),
                                       buildCapsule(template, env));

        JavaFileObject file = null;
        try {
            String capsuleClass = pkg + "." + template.getSimpleName() + "Capsule";
            file = processingEnv.getFiler().createSourceFile(capsuleClass);
            file.openWriter().append(src).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String buildCapsuleImports(TypeElement template, RoundEnvironment env)
    {
        // TODO: Visit the entirety of the definition of `template`, looking for dependencies that
        // need to be imported. Alternatively, just copy-and-paste all of the imports in the
        // original `.java` file.
        return Source.lines(0, "import java.util.concurrent.Callable;",
                               "import java.util.concurrent.Future;",
                               "import java.util.concurrent.FutureTask;",
                               "import java.util.concurrent.LinkedBlockingQueue;");
    }
    
    /**
     * @param template A handle to the original class from which a capsule is being
     * built.
     * @param env The environment in which the capsule is being built.
     * @return The source code for the capsule class which has been build to wrap the
     * original class.
     */
    private String buildCapsule(TypeElement template, RoundEnvironment env)
    {
        String pkg = getFullyQualifiedPackageName(template);
        String src = Source.lines(0, "/**",
                                     " * This capsule was auto-generated from `#0`",
                                     " */",
                                     "public class #1Capsule extends #1",
                                     "{",
                                     "#2",
                                     "}");
        return Source.format(src, pkg + "." + template.getSimpleName(),
                                        template.getSimpleName(),
                                        buildCapsuleBody(template, env));
    }

   
    private String buildCapsuleBody(TypeElement template, RoundEnvironment env)
    {
        ArrayList<String> decls = new ArrayList<String>();
        decls.add(buildCapsuleFields(template, env));
        decls.add("");
        decls.add(buildStartMethod(template, env));
        decls.add("");
        decls.add(buildRunMethod(template, env));
        decls.add("");
        
        for (Element child : template.getEnclosedElements())
        {
            // For now, ignore everything except for constructors and methods which need to be
            // wrapped with procedures.
            if (child.getKind() == ElementKind.CONSTRUCTOR) {
                decls.add(buildCapsuleFactory((ExecutableElement) child, env));
            } else if (needsProceedureWrapper(child)) {
                decls.add(buildProcedure((ExecutableElement) child, env));
            }
        }
        
        return String.join("\n", decls);
    }
    

    /**
     * Used to generate declarations for the capsule's `make()` static factory methods.
     * 
     * @param cons The constructor which is being used as a template for this factory.
     * @param env
     *
     * @return
     */
    private String buildCapsuleFactory(ExecutableElement cons, RoundEnvironment env)
    {
        /*
    	String src = Source.lines(1, "public static #0Capsule make(#1)",
                                     "{",
                                     "    #0Capsule c = new #0Capsule(#2);",
                                     "    c.start();",
                                     "    return c;",
                                     "}");
        return Source.format(src, cons.getEnclosingElement().getSimpleName(),
                                        buildProcedureParameters(cons, env),
                                        buildArgsList(cons, env));
        */
        return "";
    }


	/**
     * @param template The class from which this capsule is being built.
     * @return A string of all of the fields which the capsule needs to declare.
     */
    private String buildCapsuleFields(TypeElement templateClass, RoundEnvironment env)
    {
    	String src = Source.lines(1,
            "private LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();",
            "private {0} encapsulated = new {0}();",
            "private Thread thread;"
        );
        return MessageFormat.format(src, templateClass.getSimpleName());
    }
    
    
    private String buildStartMethod(TypeElement template, RoundEnvironment env)
    {
        /*
        return Source.lines(1, "private void start()",
                               "{",
                               "    thread = new Thread(this);",
                               "    thread.start();",
                               "}");
        */
        return "";
    }
    
    
    private String buildRunMethod(TypeElement template, RoundEnvironment env)
    {
        /*
        return Source.lines(1, "public void run()",
                               "{",
                               "    while(true) {",
                               "        // TODO",
                               "    }",
                               "}");
        */
        return "";
    }
    

    private String buildProcedure(ExecutableElement method, RoundEnvironment env)
    {
        /*
        String src = Source.lines(1, "public #0 #1(#2)",
                                     "{",
                                     "    #3",
                                     "}");
        return Source.format(src, getBoxedReturnType(method),
                                        method.getSimpleName(),
                                        buildProcedureParameters(method, env),
                                        buildCallBody(method, env));
        */
        return "";
    }
    
    
    private String buildProcedureParameters(ExecutableElement method, RoundEnvironment env)
    {
        List<String> paramStrings = new ArrayList<String>();
        for (VariableElement param : method.getParameters()) {
            paramStrings.add(buildParamDecl(param, env));
        }
        return String.join(", ", paramStrings);
    }


    private String buildParamDecl(VariableElement var, RoundEnvironment env)
    {
        return var.asType().toString() + " " + var.toString();
    }
    
    
    private String buildCallBody(ExecutableElement method, RoundEnvironment env)
    {
        String fmt;
        if (CapsuleGenerator.hasVoidReturnType(method)) {
            fmt = "encapsulated.#0(#1);";
        } else {
            fmt = "return encapsulated.#0(#1);";
        }
        return Source.format(fmt, method.getSimpleName(), buildArgsList(method, env));
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
     * Gets the fully qualified name of the given type's package.
     */
    private String getFullyQualifiedPackageName(TypeElement type)
    {
        Elements utils = processingEnv.getElementUtils();
        Name pkg = utils.getPackageOf(type).getQualifiedName();
        return pkg.toString();
    }
 
    
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
            // TODO: decide on appropriate semantics.
            if (modifiers.contains(Modifier.STATIC)) {
            	return false;
            } else if (modifiers.contains(Modifier.PUBLIC)) {
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
            return "void";
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
