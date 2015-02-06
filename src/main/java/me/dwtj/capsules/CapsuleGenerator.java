package me.dwtj.capsules;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
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
                           RoundEnvironment roundEnv)
    {
        // TODO: Why just ignore `annotations`? (See jpa-annotation-processor)
        
        Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(Capsule.class);

        processingEnv.getMessager().printMessage(Kind.NOTE, "process()");
        
        // TODO: Give warnings when the user annotates some element which cannot be a capsule.
        for (Element elem : annotated) {
            if (checkCapsule(elem, roundEnv) == true); {
                // TODO: Is this cast a problem?
                processCapsule((TypeElement) elem, roundEnv);
            }
        }
		return false;
    }


    /**
     * Processes the given capsule-annotated class in the given processing
     * environment.
     * @param elem
     * @param roundEnv
     */
    private void processCapsule(TypeElement elem, RoundEnvironment roundEnv)
    {
        compileCapsule(elem, roundEnv);
    }


    /**
     * @param elem
     * @return `true` if and only if `elem` is can be processed as a valid
     * capsule.
     */
    private static boolean checkCapsule(Element elem, RoundEnvironment roundEnv) {
        // TODO: Also double-check that the element is actually annotated with
        // `@Capsule`.
        // TODO: give warnings/errors when the user annotates some element which cannot be a capsule.
        return elem.getKind() == ElementKind.CLASS;
    }
    

    private void compileCapsule(TypeElement elem, RoundEnvironment roundEnv)
    {
        Elements utils = processingEnv.getElementUtils();
        Name pkg = utils.getPackageOf(elem).getQualifiedName();
        String capsule = elem.getSimpleName() + "Capsule";
        String qualifiedCapsule = pkg + "." + capsule;

        processingEnv.getMessager().printMessage(Kind.NOTE, "processCapsule()");
        
		String src = "package " + pkg + ";\n"
		           + "\n"
			       + "public class " + capsule + " {\n"
			       + "}";
	
        JavaFileObject file = null;
        try {

            file = processingEnv.getFiler().createSourceFile(qualifiedCapsule);
            file.openWriter().append(src).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /* Helper Methods *******************************************************/

    private TypeElement getTypeElement(String className) {
        return processingEnv.getElementUtils().getTypeElement(className);
    }
}
