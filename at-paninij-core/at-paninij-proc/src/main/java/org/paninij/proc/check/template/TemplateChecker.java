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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.PaniniProcessor;
import org.paninij.proc.check.FailureBehavior;
import org.paninij.proc.check.Result;


public class TemplateChecker
{
    protected final TemplateCheck templateChecks[];
    protected final TemplateCheckEnvironment env;
    protected final FailureBehavior failureBehavior;
    
    public TemplateChecker(ProcessingEnvironment procEnv, FailureBehavior errorBehavior)
    {
        this.env = new TemplateCheckEnvironment(procEnv);
        this.failureBehavior = errorBehavior;
        
        templateChecks = new TemplateCheck[]
        {
            new SuffixCheck(),
            new NotSubclassCheck(env),
            new NoVariadicMethodsCheck(),
            new OnlyZeroArgConstructorsCheck(),
            new NoMainCheck(),
            new InitDeclCheck(),
            new NoNestedTypesCheck(),
            new NoTypeParamCheck(),
            new NoIllegalModifiersCheck()
        };
    }
    

    /**
     * @param template
     * @return `true` if and only if `template` is can be processed as a valid capsule template.
     */
    public boolean check(PaniniProcessor context, Element template)
    {
        if (template.getKind() != ElementKind.CLASS)
        {
            // TODO: Make this error message a bit clearer.
            // TODO: Switch between error behaviors.
            context.error("A capsule template must be a class, but an element annotated with `@Capsule` is of kind " + template.getKind());
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
