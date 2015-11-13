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
package org.paninij.proc.check.capsule;

import static java.text.MessageFormat.format;

import static org.paninij.proc.check.Result.ok;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.paninij.lang.Capsule;
import org.paninij.proc.check.Check;
import org.paninij.proc.check.CheckEnvironment;
import org.paninij.proc.check.NoBadMethodNamesCheck;
import org.paninij.proc.check.NoNestedTypesCheck;
import org.paninij.proc.check.NoTypeParamCheck;
import org.paninij.proc.check.NotSubclassCheck;
import org.paninij.proc.check.ProcReturnTypesDuckabilityCheck;
import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;
import org.paninij.proc.check.SuffixCheck;


public class CapsuleChecker implements Check
{
    protected final CapsuleCheck capsuleChecks[];
    protected final CheckEnvironment env;
    
    public CapsuleChecker(ProcessingEnvironment procEnv, RoundEnvironment roundEnv)
    {
        this.env = new CheckEnvironment(procEnv, roundEnv);
        
        capsuleChecks = new CapsuleCheck[]
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
            new ProcReturnTypesDuckabilityCheck(env),
            new NoImportedFieldsOnRootCheck(),
            new NoBadMethodNamesCheck(),
        };
    }
    

    /**
     * @param template
     * @return `true` if and only if `template` is can be processed as a valid capsule template.
     */
    public Result check(Element template)
    {
        if (template.getAnnotation(Capsule.class) == null) {
            String err = "Tried to check an element as a capsule template though it is not "
                       + "annotated with `@Capsule`: " + template;
            throw new IllegalArgumentException(err);
        }
        
        // Check to see if we can cast the given element to a type element.
        if (template.getKind() != ElementKind.CLASS)
        {
            String err = "A capsule template must be a class, but an element annotated with "
                       + "`@Capsule` named `{0}` is of kind {1}.";
            err = format(err, template, template.getKind());
            return new Error(err, CapsuleChecker.class, template);
        }

        for (CapsuleCheck check: capsuleChecks)
        {
            Result result = check.checkCapsule((TypeElement) template);
            if (!result.ok()) {
                return result;
            }
        }

        return ok;
    }
}
