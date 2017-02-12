package org.paninij.proc;

import org.paninij.lang.CapsuleInterface;
import org.paninij.lang.SignatureInterface;
import org.paninij.proc.check.Check.Result;
import org.paninij.proc.check.capsule.CapsuleCheck;
import org.paninij.proc.check.capsule.CheckForCycleOfLocalFields;
import org.paninij.proc.check.capsule.RoundOneCapsuleChecks;
import org.paninij.proc.factory.CapsuleMonitorFactory;
import org.paninij.proc.factory.CapsuleSerialFactory;
import org.paninij.proc.factory.CapsuleTaskFactory;
import org.paninij.proc.factory.CapsuleThreadFactory;
import org.paninij.proc.factory.MessageFactory;
import org.paninij.proc.model.Capsule;
import org.paninij.proc.model.CapsuleElement;
import org.paninij.proc.model.Procedure;
import org.paninij.proc.model.Signature;
import org.paninij.proc.model.SignatureElement;
import org.paninij.proc.util.ArtifactFiler;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.HashSet;
import java.util.Set;

import static org.paninij.proc.util.PaniniModel.CAPSULE_CORE_SUFFIX;
import static org.paninij.proc.util.PaniniModel.SIGNATURE_SPEC_SUFFIX;

/**
 * @author dwtj
 */
@SupportedAnnotationTypes({"org.paninij.lang.CapsuleInterface",
                           "org.paninij.lang.SignatureInterface"})
@SupportedOptions({})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RoundOneProcessor extends AbstractProcessor {

    // Standard utilities from the processing environment instance:
    private Types typeUtils;
    private Elements elementUtils;

    // Custom utilities instantiated using the processing environment instance:
    private RoundOneCapsuleChecks capsuleCheck;
    private CapsuleCheck cycleCheck;
    private ArtifactFiler artifactMaker;


    // Factories to perform code generating:
    private final MessageFactory messageFactory = new MessageFactory();
    private final CapsuleThreadFactory capsuleThreadFactory = new CapsuleThreadFactory();
    private final CapsuleSerialFactory capsuleSerialFactory = new CapsuleSerialFactory();
    private final CapsuleMonitorFactory capsuleMonitorFactory = new CapsuleMonitorFactory();
    private final CapsuleTaskFactory capsuleTaskFactory = new CapsuleTaskFactory();

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();

        capsuleCheck = new RoundOneCapsuleChecks(processingEnv);
        cycleCheck = new CheckForCycleOfLocalFields(processingEnv);
        artifactMaker = new ArtifactFiler(processingEnv.getFiler());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        if (roundEnv.processingOver()) {
            artifactMaker.close();
            return false;
        }

        // TODO: What about if an error was raised in the prior round? See: roundEnv.errorRaised()

        // Perform all remaining code-gen on OK capsule cores:
        for (TypeElement core : getOkCapsuleCores(roundEnv)) {
            Capsule model = CapsuleElement.make(core);
            for (Procedure procedure : model.getProcedures()) {
                artifactMaker.add(messageFactory.make(procedure));
            }
            artifactMaker.add(capsuleThreadFactory.make(model));
            artifactMaker.add(capsuleSerialFactory.make(model));
            artifactMaker.add(capsuleMonitorFactory.make(model));
            artifactMaker.add(capsuleTaskFactory.make(model));
        }

        // Perform all remaining code-gen on OK signature cores:
        for (TypeElement core : getOkSignatureCores(roundEnv)) {
            Signature model = SignatureElement.make(core);
            for (Procedure procedure : model.getProcedures()) {
                artifactMaker.add(messageFactory.make(procedure));
            }
        }

        artifactMaker.makeAll();
        return false;
    }

    /**
     * <p>Get all OK capsule cores whose capsule interfaces were generated in the last round. We
     * consider a capsule core to be OK if it passes all checks (i.e. all checks return an OK
     * {@link Result}).
     *
     * <p>If some capsule core whose capsule interface was generated in the last round does not
     * pass some check, then this method has the side effect of reporting this failed check via
     * {@link #error}.
     *
     * <p>Code generation ought to be able to be performed on all capsule cores returned from
     * this method.
     *
     * @param roundEnv
     *          The current round environment, in which we lookup capsule interfaces generated in
     *          the last round.
     * @return
     *          A newly instantiated set of type elements of capsule cores adhering to the
     *          above description.
     */
    private Set<TypeElement> getOkCapsuleCores(RoundEnvironment roundEnv) {
        Set<TypeElement> set = new HashSet<>();

        for (Element iface : roundEnv.getElementsAnnotatedWith(CapsuleInterface.class)) {
            String coreName = iface + CAPSULE_CORE_SUFFIX;
            TypeElement core = elementUtils.getTypeElement(coreName);
            if (core == null) {
                String msg = "Found a capsule interface, but could not find its corresponding "
                           + "capsule core: " + coreName;
                throw new IllegalStateException(msg);
            }

            Result result = capsuleCheck.checkCapsule(core);
            if (result.ok()) {
                set.add(core);
            } else {
                error(result.errMsg(), result.offender());
            }
        }

        // Run one more check. This check is not part of the `capsuleCheck` above, because some of
        // the prior checks need to have already been performed before this check will behave
        // correctly.
        Set<TypeElement> filteredSet = new HashSet<>();
        for (TypeElement core : set) {
            Result result = cycleCheck.checkCapsule(core);
            if (result.ok()) {
                filteredSet.add(core);
            } else {
                error(result.errMsg(), result.offender());
            }
        }

        return filteredSet;
    }

    /**
     * Just like {@link #getOkCapsuleCores(RoundEnvironment)} but for signature cores.
     */
    private Set<TypeElement> getOkSignatureCores(RoundEnvironment roundEnv) {
        // Note: In the current implementation, all signature core checks will have already been
        // performed in the previous round, and no more checks need to be performed here.
        Set<TypeElement> set = new HashSet<>();
        for (Element iface : roundEnv.getElementsAnnotatedWith(SignatureInterface.class)) {
            String coreName = iface + SIGNATURE_SPEC_SUFFIX;
            TypeElement core = elementUtils.getTypeElement(coreName);
            if (core == null) {
                String msg = "Found a signature interface, but could not find its corresponding "
                           + "signature core: " + coreName;
                throw new IllegalStateException(msg);
            }
            set.add(core);
        }
        return set;
    }

    public void error(String msg) {
        processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, msg);
    }

    public void error(String msg, Element offender) {
        processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, msg, offender);
    }
}
