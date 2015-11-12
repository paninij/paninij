package org.paninij.proc.check.capsule;

import static java.text.MessageFormat.format;
import static javax.lang.model.element.ElementKind.METHOD;

import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;

/**
 * For each of the three `@PaniniJ` declarations kinds (`run()`, `init()`, and `design()`) this
 * checks that there are only zero or one of declaration of that kind in a capsule template.
 */
public class NotTooManyDeclsCheck implements CapsuleCheck
{
    @Override
    public Result checkCapsule(TypeElement template)
    {
        int run = 0;
        int init = 0;
        int design = 0;
        
        for (Element elem : template.getEnclosedElements()) {
            if (elem.getKind() == METHOD) {
                switch (elem.getSimpleName().toString()) {
                case "run":
                    run++;
                    continue;
                case "init":
                    init++;
                    continue;
                case "design":
                    design++;
                    continue;
                default:
                    continue;
                }
            }
        }
        if (run > 1) {
            return error(template, "run", run);
        }
        if (init > 1) {
            return error(template, "init", init);
        }
        if (design > 1) {
            return error(template, "design", design);
        }

        return ok;
    }
    
    private static Result error(TypeElement template, String decl, int numFound)
    {
        String err = "A capsule template must contain either 0 or 1 `{0}()` methods (a.k.a. {0} "
                   + "declarations), but {1} `{0}()` methods were found in capsule template `{2}`.";
        err = format(err, decl, numFound, template.getSimpleName().toString());
        return new Error(err, NotTooManyDeclsCheck.class, template);
    }
}
