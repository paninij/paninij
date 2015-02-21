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

import org.paninij.lang.Signature;
import org.paninij.util.Source;


/**
 * Used as a service during compilation to makes an automatically generated
 * file for each class annotated with `@Signature`.
 */
@SupportedAnnotationTypes("org.paninij.lang.Signature")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SignatureGenerator extends AbstractProcessor
{
    private TypeElement signatureTypeElement;  // Should not be modified after set in `init()`.

    public void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        signatureTypeElement = getTypeElement("org.paninij.lang.Signature");
    }
    
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment env)
    {
        // TODO: Why just ignore `annotations`? (See jpa-annotation-processor)
        
        Set<? extends Element> annotated = env.getElementsAnnotatedWith(Signature.class);

        // TODO: Give warnings when the user annotates some element which cannot be a signature.
        for (Element elem : annotated) {
            if (checkSignature(elem, env) == true) {
                // TODO: Is this cast a problem?
                processSignature((TypeElement) elem, env);
            }
        }
        return false;
    }


    /**
     * Processes the given signature-annotated class in the given processing environment.
     * @param elem
     * @param env
     */
    private void processSignature(TypeElement elem, RoundEnvironment env)
    {
        makeSignatureFile(elem, env);
    }


    /**
     * @param elem
     * @return `true` if and only if `elem` is can be processed as a valid signature.
     */
    private static boolean checkSignature(Element elem, RoundEnvironment env) {
        // TODO: Also double-check that the element is actually annotated with `@Signature`.
        // TODO: give errors when the user annotates an element which cannot be a signature.
        // TODO: check that the class does not have any inner classes.
        return elem.getKind() == ElementKind.INTERFACE;
    }
    

    private void makeSignatureFile(TypeElement template, RoundEnvironment env)
    {
        String pkg = getFullyQualifiedPackageName(template);
        String src = Source.lines(0, "package #0;",
                                     "",
                                     "#1",
                                     "",
                                     "",
                                     "#2");

        src = Source.format(src, pkg, buildSignatureImports(template, env),
                            buildSignature(template, env));

        JavaFileObject file = null;
        try {
            String signatureClass = pkg + "." + template.getSimpleName() + "Signature";
            file = processingEnv.getFiler().createSourceFile(signatureClass);
            file.openWriter().append(src).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String buildSignatureImports(TypeElement template, RoundEnvironment env)
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
     * @param template A handle to the original class from which a signature is being built.
     * @param env The environment in which the signature is being built.
     * @return The source code for the signature class which has been build to wrap the
     * original class.
     */
    private String buildSignature(TypeElement template, RoundEnvironment env)
    {
        String pkg = getFullyQualifiedPackageName(template);
        String src = Source.lines(0, "/**",
                                     " * This signature was auto-generated from `#0`",
                                     " */",
                                     "public interface #1Signature",
                                     "{",
                                     "",
                                     "    void start();",
                                     "    void run();",
                                     "",
                                     "#2",
                                     "}");
        return Source.format(src, pkg + "." + template.getSimpleName(),
                                        template.getSimpleName(),
                                        buildSignatureBody(template, env));
    }

   
    private String buildSignatureBody(TypeElement template, RoundEnvironment env)
    {
        ArrayList<String> decls = new ArrayList<String>();
        
        for (Element child : template.getEnclosedElements())
        {
            // For now, ignore everything except for constructors and methods which need to be
            // wrapped with procedures.
            if (child.getKind() == ElementKind.CONSTRUCTOR) {
                decls.add(buildSignatureFactory((ExecutableElement) child, env));
            } else if (needsProceedureWrapper(child)) {
                decls.add(buildProcedure((ExecutableElement) child, env));
            }
        }
        
        return String.join("\n", decls);
    }
    

    /**
     * Used to generate declarations for the signature's `make()` static factory methods.
     * 
     * @param cons The constructor which is being used as a template for this factory.
     * @param env
     *
     * @return
     */
    private String buildSignatureFactory(ExecutableElement cons, RoundEnvironment env)
    {
        return Source.format("    public static #0Signature make(#1);",
                             cons.getEnclosingElement().getSimpleName(),
                             buildProcedureParameters(cons, env));
    }


    private String buildProcedure(ExecutableElement method, RoundEnvironment env)
    {
        return Source.format("    public Future<#0> #1(#2);",
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

   
    
    /* Helper Methods ****************************************************************************/

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
     * @return `true` iff the given `elem` is a method which needs to be wrapped as a procedure.
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
