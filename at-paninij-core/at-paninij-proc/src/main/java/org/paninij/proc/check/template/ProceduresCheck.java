package org.paninij.proc.check.template;

import static java.text.MessageFormat.format;

import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;


/**
 * If the given template has a run declaration (i.e. some method named "run"), then the template is
 * interpreted to represent an active capsule. Otherwise it is interpreted to be a passive capsule.
 * If given an active capsule, then this check ensures that the template has no procedures.
 * Otherwise, if given a passive capsule, this check ensures that every procedure is well-formed.
 */
public class ProceduresCheck implements TemplateCheck
{
    public final static String ERROR_SOURCE = ProceduresCheck.class.getName();
    
    private final static String[] DECLARATION_NAMES = {
        "init",
        "design",
        "run",
    };

    @Override
    public Result check(TypeElement template) {
        return isActive(template) ? checkActiveTemplate(template) : checkPassiveTemplate(template);
    }
    
    private static boolean isActive(TypeElement template)
    {
        for (Element elem: template.getEnclosedElements()) {
            if (elem.getKind() == METHOD && elem.getSimpleName().toString().equals("run")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check that there are no procedures.
     */
    private Result checkActiveTemplate(TypeElement template)
    {
        for (Element elem: template.getEnclosedElements()) {
            if (isProcedure(elem)) {
                String err = "Active templates cannot have procedures, but a procedure named `{0}` "
                           + "was found in active template `{1}`.";
                err = format(err, elem.getSimpleName(), template.getSimpleName());
                return new Error(err, ERROR_SOURCE);
            }
        }
        return ok;
    }

    /**
     * Check that every procedure defined on the given passive capsule is well-formed.
     */
    private Result checkPassiveTemplate(TypeElement template)
    {
        for (Element elem: template.getEnclosedElements()) {
            if (isProcedure(elem)) {
                Result result = checkProcedure(template, (ExecutableElement) elem);
                if (!result.ok()) {
                    return result;
                }
            }
        }
        return ok;
    }
    
    /**
     * Check that a single procedure defined on the given passive capsule is well-formed.
     */
    private Result checkProcedure(TypeElement template, ExecutableElement procedure)
    {
        if (!procedure.getModifiers().contains(PUBLIC)) {
            String err = "Procedures must be declared `public`, but a procedure named `{0}` in the "
                       + "capsule template `{1}` is not declared `public`.";
            err = format(err, procedure.getSimpleName(), template.getSimpleName());
            return new Error(err, ERROR_SOURCE);
        }
        return ok;
    }
    
    /**
     * @return  `true` iff the given element represents a procedure (i.e. it is a method which is
     *          not `static`, not `private`, and not some kind of `@PaniniJ` declaration).
     */
    private boolean isProcedure(Element elem)
    {
        if (elem.getKind() != METHOD) {
            return false;
        }
        if (elem.getModifiers().contains(STATIC) || elem.getModifiers().contains(PRIVATE)) {
            return false;
        }
        for (String name : DECLARATION_NAMES) {
            if (elem.getSimpleName().toString().equals(name)) {
                return false;
            }
        }
        return true;
    }
}
