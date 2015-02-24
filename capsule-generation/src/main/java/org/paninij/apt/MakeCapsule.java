package org.paninij.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;


abstract class MakeCapsule
{
    PaniniPress context;
    TypeElement template;

    /**
     * Factory method. This must be overriden in concrete subclasses.
     *
     * @param context The PaniniPress object in which in which the capsule is being built.
     * @param template A handle to the original class from which a capsule is being built.
     */
    static MakeCapsule make(PaniniPress context, TypeElement template) {
        throw new UnsupportedOperationException("Cannot instantiate an abstract class.");
    }


    /**
     * Generates the source code for a capsule class from the template class (using the
     * `buildCapsule()` method; then saves the resulting source code to a file to be compiled later
     * (sometime after the current processor has finished).
     */
    void makeSourceFile()
    {
        String capsuleName = buildQualifiedCapsuleName();
        context.createJavaFile(capsuleName, buildCapsule());
    }

    abstract String buildCapsule();

    abstract String buildCapsuleName();

    String buildPackage() {
        return context.getPackageOf(template);
    }
 
    abstract String buildQualifiedCapsuleName();

    abstract String buildCapsuleImports();
 
    abstract String buildCapsuleDecl();

    abstract String buildCapsuleBody();
    
    /**
     * @return A string of all of the fields which the capsule needs to declare.
     */
    abstract String buildCapsuleFields();

    abstract String buildProcedure(ExecutableElement method);
    
    abstract String buildProcedureParameters(ExecutableElement method);

    abstract String buildArgsList(ExecutableElement method);

    abstract String buildParamDecl(VariableElement param);
}
