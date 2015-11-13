package org.paninij.proc.check.template;

import static java.text.MessageFormat.format;

import static javax.lang.model.element.ElementKind.METHOD;

import static org.paninij.proc.check.Result.ok;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.paninij.lang.Block;
import org.paninij.lang.Future;
import org.paninij.proc.check.CheckEnvironment;
import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;
import org.paninij.proc.check.duckability.DuckabilityChecker;

/**
 * Uses a `DuckabilityChecker` to check the duckability of the return types of all `@Duck`
 * procedures of a given capsule template or signature template.
 */
public class ProcReturnTypesDuckabilityCheck extends AbstractTemplateCheck
{
    private final DuckabilityChecker checker;

    public ProcReturnTypesDuckabilityCheck(CheckEnvironment env) {
        this.checker = new DuckabilityChecker(env);
    }

    @Override
    protected Result checkTemplate(TemplateKind templateKind, TypeElement template)
    {
        // Iterate over all methods, and for those which are `@Duck` procedures (either explicitly
        // or implicitly), check their return type's duckability.
        for (Element elem : template.getEnclosedElements()) {
            if (isDuckProcedure(elem)) {
                Result result = checker.check(((ExecutableElement) elem).getReturnType());
                if (! result.ok()) {
                    String err = "A {0} template has a procedure whose return type cannot be "
                               + "ducked. {1}";
                    err = format(err, templateKind, result.err());
                    return new Error(err, ProcReturnTypesDuckabilityCheck.class, elem);
                }
            }
        }
        return ok;
    }
    
    private static boolean isDuckProcedure(Element member)
    {
        String name = member.getSimpleName().toString();
        return member.getKind() == METHOD
            && !isDeclName(name)
            && !hasAnnotation(member, Future.class)
            && !hasAnnotation(member, Block.class);
    }
    
    private static boolean isDeclName(String name) {
        return name.equals("init")
            || name.equals("design")
            || name.equals("run");
    }
    
    private static <A extends Annotation> boolean hasAnnotation(Element elem, Class<A> anno) {
        return elem.getAnnotation(anno) != null;
    }
}
