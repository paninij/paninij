package org.paninij.proc.check.capsule;

import org.paninij.proc.check.capsule.decl.CheckDesignDecl;
import org.paninij.proc.check.capsule.decl.CheckInitDecl;
import org.paninij.proc.check.capsule.decl.CheckRunDecl;
import org.paninij.proc.check.template.CheckForBadAnnotations;
import org.paninij.proc.check.template.CheckForIllegalMethodNames;
import org.paninij.proc.check.template.CheckForIllegalSubtyping;
import org.paninij.proc.check.template.CheckForNestedTypes;
import org.paninij.proc.check.template.CheckForTypeParameters;
import org.paninij.proc.check.template.CheckPackage;
import org.paninij.proc.check.template.CheckProcAnnotations;
import org.paninij.proc.check.template.CheckSuffix;
import org.paninij.proc.check.template.TemplateCheck;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.util.JavaModel.isAnnotatedBy;


/**
 * Performs some initial checks on capsule and signature template at the beginning of the round in
 * which those sources are first seen.
 *
 * @author dwtj
 */
public class RoundZeroCapsuleChecks implements CapsuleCheck {

    private final ProcessingEnvironment procEnv;
    private final CapsuleCheck roundZeroChecks[];

    public RoundZeroCapsuleChecks(ProcessingEnvironment procEnv) {
        this.procEnv = procEnv;
        this.roundZeroChecks = new CapsuleCheck[] {
            new CheckSuffix(),
            new CheckPackage(),
            new CheckProcedures(),
            new CheckProcAnnotations(procEnv),
            new CheckForIllegalSubtyping(procEnv),
            new CheckForNonZeroArgConstructors(),
            new CheckForTooManyDecls(),
            new CheckInitDecl(),
            new CheckRunDecl(),
            new CheckDesignDecl(),
            new CheckForNestedTypes(),
            new CheckForTypeParameters(),
            new CheckForIllegalModifiers(),
            new CheckForIllegalMethodNames(),
            new CheckForBadAnnotations(),
            new CheckThatOnlySignatureTemplatesAreImplemented(procEnv),
        };
    }


    @Override
    public Result checkCapsule(TypeElement template) {
        if (! isAnnotatedBy(procEnv, template, "org.paninij.lang.Capsule")) {
            String err = "Tried to check an element as a capsule template, but it is not " +
                         "annotated with `@Capsule`: " + template.getQualifiedName();
            throw new IllegalArgumentException(err);
        }
        for (CapsuleCheck check : roundZeroChecks) {
            Result result = check.checkCapsule(template);
            if (! result.ok()) {
                return result;
            }
        }
        return OK;
    }
}
