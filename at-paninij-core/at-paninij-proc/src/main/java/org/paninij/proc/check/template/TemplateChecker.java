/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills, David Johnston, Trey Erenberger
 */
package org.paninij.proc.check.template;

import static java.text.MessageFormat.format;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.paninij.lang.Capsule;
import org.paninij.proc.PaniniProcessor;
import org.paninij.proc.check.FailureBehavior;
import org.paninij.proc.check.Result;


public class TemplateChecker
{
    protected final TemplateCheck templateChecks[];
    protected final TemplateCheckEnvironment env;
    protected final FailureBehavior failureBehavior;
    
    public TemplateChecker(ProcessingEnvironment procEnv, RoundEnvironment roundEnv,
                           FailureBehavior failureBehavior)
    {
        this.env = new TemplateCheckEnvironment(procEnv, roundEnv);
        this.failureBehavior = failureBehavior;
        
        templateChecks = new TemplateCheck[]
        {
            new SuffixCheck(),
            new NotSubclassCheck(env),
            new NoVariadicMethodsCheck(),
            new OnlyZeroArgConstructorsCheck(),
            new NotTooManyDeclsCheck(),
            new InitDeclCheck(),
            new RunDeclCheck(),
            new DesignDeclCheck(),
            new NoNestedTypesCheck(),
            new NoTypeParamCheck(),
            new NoIllegalModifiersCheck(),
            new ProceduresCheck(),
            new FieldsCheck(env),
            new ImplementedSignaturesCheck(env),
        };
    }
    

    /**
     * @param template
     * @return `true` if and only if `template` is can be processed as a valid capsule template.
     */
    public boolean check(PaniniProcessor context, Element template)
    {
        if (template.getAnnotation(Capsule.class) == null) {
            String err = "Tried to check an element as a capsule template though it is not "
                       + "annotated with `@Capsule`: " + template;
            throw new IllegalArgumentException(err);
        }
        
        if (template.getKind() != ElementKind.CLASS)
        {
            // TODO: Make this error message a bit clearer.
            // TODO: Switch between error behaviors.
            String err = "A capsule template must be a class, but an element annotated with "
                       + "`@Capsule` named `{0}` is of kind {1}.";
            err = format(err, template, template.getKind());
            context.error(err);

            return false;
        }

        for (TemplateCheck check: templateChecks)
        {
            Result result = check.check((TypeElement) template);
            if (!result.ok())
            {
                switch (failureBehavior) {
                case LOGGING:
                    context.error(result.err());
                    context.error("Error Source: " + result.source());
                    break;
                case EXCEPTION:
                    throw new TemplateCheckException(result.err());
                }
            }
        }

        return true;
    }
}
