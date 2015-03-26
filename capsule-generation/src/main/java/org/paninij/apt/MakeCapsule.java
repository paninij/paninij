package org.paninij.apt;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.paninij.apt.util.Source;
import org.paninij.apt.util.TypeCollector;


abstract class MakeCapsule
{
    PaniniPress context;
    TypeElement template;

    /**
     * Factory method. This must be overridden in concrete subclasses.
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

    String buildCapsuleImports()
    {
        Set<String> imports = TypeCollector.collect(template);
        imports.addAll(getStandardImports());
        return Source.buildImportDecls(imports);
    }

    abstract String buildCapsuleDecl();

    abstract String buildCapsuleBody();
    
    /**
     * @return A string of all of the fields which the capsule needs to declare.
     */
    abstract String buildCapsuleFields();

    abstract String buildProcedure(ExecutableElement method);
    
    /**
     * In this default implementation, an empty set is always returned.
     * 
     * @return The set of imports which every capsule will need to import, where each import is
     * represented by a `String` of the fully qualified class name.
     */
    Set<String> getStandardImports() {
        return new HashSet<String>();
    }
}
