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

        processingEnv.getMessager().printMessage(Kind.NOTE, "process()");
        
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
        // TODO: give warnings/errors when the user annotates some element which cannot be a capsule.
        // TODO: check that the class does not have any inner classes.
        return elem.getKind() == ElementKind.CLASS;
    }
    

    private void makeCapsuleFile(TypeElement elem, RoundEnvironment env)
    {
        Elements utils = processingEnv.getElementUtils();

        Name pkg = utils.getPackageOf(elem).getQualifiedName();
        
        String src = "package {0};\n"
                     + "\n"
                     + "{1}";

        src = MessageFormat.format(src, pkg, buildCapsule(elem, env));

        JavaFileObject file = null;
        try {
            String capsuleClass = pkg + "." + elem.getSimpleName() + "Capsule";
            file = processingEnv.getFiler().createSourceFile(capsuleClass);
            file.openWriter().append(src).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        Elements utils = processingEnv.getElementUtils();
        Name pkg = utils.getPackageOf(orig).getQualifiedName();
        String qualifiedOrig = pkg + "." + orig.getSimpleName();

        processingEnv.getMessager().printMessage(Kind.NOTE, "processCapsule()");

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
                       + " * This class was auto-generated using the `@Capsule` annotation processor from\n"
                       + " * `{0}`\n"
                       + " */\n";
        return MessageFormat.format(comment, qualifiedOriginal);
    }
    

    private String buildCapsuleDecl(TypeElement origElem, RoundEnvironment env)
    {
        Name origName = origElem.getSimpleName();
        return MessageFormat.format("public class {0}Capsule extends {1}", origName, origName);
    }


    private String buildCapsuleBody(TypeElement orig, RoundEnvironment env)
    {
        String rv = "";

        for (Element child : orig.getEnclosedElements()) {
            // For now, ignore everything except for constructors and methods
            // which need to be wrapped.
            if (child.getKind() == ElementKind.CONSTRUCTOR) {
                // TODO
                rv += "    // TODO: build constructor\n\n";
            } else if (needsProceedureWrapper(child)) {
                rv += buildProcedure((ExecutableElement) child, env);
            }
        }
        
        return rv;
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
        return MessageFormat.format("    public {0} {1}Proc({2})",
                                    method.getReturnType(),
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
     * 
     * @param method The method which the proceedure being build is wrapping.
     * @param env
     * @return
     */
    private String buildProcedureBody(ExecutableElement method, RoundEnvironment env)
    {
    	String fmt = "        ";
    	if (method.getReturnType().toString() != "void") {
    		fmt += "return ";
    	}
    	fmt += "{0}({1});\n";
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

    
    private TypeElement getTypeElement(String className) {
        return processingEnv.getElementUtils().getTypeElement(className);
    }
}
