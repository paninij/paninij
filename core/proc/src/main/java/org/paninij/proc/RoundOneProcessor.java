package org.paninij.proc;

import org.paninij.lang.CapsuleInterface;
import org.paninij.lang.SignatureInterface;
import org.paninij.proc.check.Check;
import org.paninij.proc.check.CheckException;
import org.paninij.proc.check.capsule.RoundOneCapsuleChecks;
import org.paninij.proc.check.signature.AllSignatureChecks;
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
import java.util.Set;

import static org.paninij.proc.util.PaniniModel.CAPSULE_TEMPLATE_SUFFIX;
import static org.paninij.proc.util.PaniniModel.SIGNATURE_TEMPLATE_SUFFIX;

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
    private AllSignatureChecks signatureCheck;
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
        signatureCheck = new AllSignatureChecks(processingEnv);
        artifactMaker = new ArtifactFiler(processingEnv.getFiler());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        if (roundEnv.processingOver()) {
            artifactMaker.close();
            return false;
        }

        // TODO: Check everything, but then only do code-gen if everything checks out?

        // For each capsule interface generated in the last round, lookup the capsule template from
        // which it was created. Check the capsule template. If it checks-out, then start code-gen.
        // Otherwise, report an error. Note that capsule interface sources were already created in
        // the last round.
        for (Element iface : roundEnv.getElementsAnnotatedWith(CapsuleInterface.class)) {
            String templateName = iface + CAPSULE_TEMPLATE_SUFFIX;
            TypeElement template = elementUtils.getTypeElement(templateName);
            if (template == null) {
                String msg = "Found a capsule interface, but could not find its corresponding "
                           + "capsule template: " + templateName;
                throw new IllegalStateException(msg);
            }
            Check.Result result = capsuleCheck.checkCapsule(template);
            if (! result.ok()) {
                // TODO: Re-enable once compile test improvements make `CheckException` obsolete.
                //error(result.errMsg(), result.offender());
                throw new CheckException();
            } else {
                Capsule model = CapsuleElement.make(template);
                for (Procedure procedure : model.getProcedures()) {
                    artifactMaker.add(messageFactory.make(procedure));
                }
                artifactMaker.add(capsuleThreadFactory.make(model));
                artifactMaker.add(capsuleSerialFactory.make(model));
                artifactMaker.add(capsuleMonitorFactory.make(model));
                artifactMaker.add(capsuleTaskFactory.make(model));
            }
        }

        // For each signature interface generated in the last round, lookup the capsule template
        // from which it was created. Note that unlike for capsules above, we do not need to check
        // any signature templates here: all signature templates must have been checked in the
        // previous round. Note also that signature interface sources were already created in the
        // last round. Thus, the only sources which still need to be generated from signatures are
        // the messages.
        for (Element iface : roundEnv.getElementsAnnotatedWith(SignatureInterface.class)) {
            String templateName = iface + SIGNATURE_TEMPLATE_SUFFIX;
            TypeElement template = elementUtils.getTypeElement(templateName);
            if (template == null) {
                String msg = "Found a signature interface, but could not find its corresponding "
                           + "signature template: " + templateName;
                throw new IllegalStateException(msg);
            }
            Signature model = SignatureElement.make(template);
            for (Procedure procedure : model.getProcedures()) {
                artifactMaker.add(messageFactory.make(procedure));
            }
        }

        artifactMaker.makeAll();

        return false;
    }

    public void error(String msg) {
        processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, msg);
    }

    public void error(String msg, Element offender) {
        processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, msg, offender);
    }
}
