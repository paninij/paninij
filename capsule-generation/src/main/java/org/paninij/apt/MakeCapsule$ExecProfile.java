package org.paninij.apt;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;


/**
 * An abstract class which is extended by each `MakeCapsule$*` class; each concrete subclass (e.g.
 * `MakeCapsule$Thread`) is meant to inspect a given capsule template and produce a capsule artifact
 * with that execution profile.
 */
abstract class MakeCapsule$ExecProfile
{
    PaniniPress context;
    TypeElement template;

    /**
     * Factory method. This must be overridden in concrete subclasses.
     *
     * @param context The PaniniPress object in which in which the capsule is being built.
     * @param template A handle to the original class from which a capsule is being built.
     */
    static MakeCapsule$ExecProfile make(PaniniPress context, TypeElement template) {
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
}
