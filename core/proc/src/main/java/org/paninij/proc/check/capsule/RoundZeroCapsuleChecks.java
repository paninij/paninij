package org.paninij.proc.check.capsule;

import org.paninij.proc.check.capsule.decl.CheckDesignDecl;
import org.paninij.proc.check.capsule.decl.CheckInitDecl;
import org.paninij.proc.check.capsule.decl.CheckRunDecl;
import org.paninij.proc.check.core.CheckForBadAnnotations;
import org.paninij.proc.check.core.CheckForIllegalMethodNames;
import org.paninij.proc.check.core.CheckForIllegalSubtyping;
import org.paninij.proc.check.core.CheckForNestedTypes;
import org.paninij.proc.check.core.CheckForTypeParameters;
import org.paninij.proc.check.core.CheckPackage;
import org.paninij.proc.check.core.CheckProcAnnotations;
import org.paninij.proc.check.core.CheckSuffix;
import org.paninij.proc.check.core.CoreCheck;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.util.JavaModel.isAnnotatedBy;


/**
 * Performs some initial checks on capsule and signature core at the beginning of the round in
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
            new CheckThatOnlySignatureCoresAreImplemented(procEnv),
            new CheckHandlers(),
            new CheckEventFields(),
        };
    }


    @Override
    public Result checkCapsule(TypeElement core) {
        if (! isAnnotatedBy(procEnv, core, "org.paninij.lang.Capsule")) {
            String err = "Tried to check an element as a capsule core, but it is not " +
                         "annotated with `@Capsule`: " + core.getQualifiedName();
            throw new IllegalArgumentException(err);
        }
        for (CapsuleCheck check : roundZeroChecks) {
            Result result = check.checkCapsule(core);
            if (! result.ok()) {
                return result;
            }
        }
        return OK;
    }
}
