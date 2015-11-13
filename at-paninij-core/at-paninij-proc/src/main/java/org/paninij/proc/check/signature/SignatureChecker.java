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
package org.paninij.proc.check.signature;

import static java.text.MessageFormat.format;

import static org.paninij.proc.check.Result.ok;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.paninij.lang.Signature;
import org.paninij.proc.check.Check;
import org.paninij.proc.check.CheckEnvironment;
import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;
import org.paninij.proc.check.template.NoBadMethodNamesCheck;
import org.paninij.proc.check.template.NoNestedTypesCheck;
import org.paninij.proc.check.template.NoTypeParamCheck;
import org.paninij.proc.check.template.NotSubclassCheck;
import org.paninij.proc.check.template.ProcReturnTypesDuckabilityCheck;
import org.paninij.proc.check.template.SuffixCheck;


public class SignatureChecker implements Check
{
    protected final SignatureCheck signatureChecks[];
    protected final CheckEnvironment env;
    
    public SignatureChecker(ProcessingEnvironment procEnv, RoundEnvironment roundEnv)
    {
        this.env = new CheckEnvironment(procEnv, roundEnv);
        
        signatureChecks = new SignatureCheck[]
        {
            new SuffixCheck(),
            new NoNestedTypesCheck(),
            new NoRootAnnotationCheck(),
            new NotSubclassCheck(env),
            new NoTypeParamCheck(),
            new NoIllegalModifiersCheck(),
            new NoIllegalNamesCheck(),
            new ProcReturnTypesDuckabilityCheck(env),
            new NoBadMethodNamesCheck(),
        };
    }
    

    /**
     * @param  template  An element to be checked as a signature template.
     * @return An `ok` result if and only if `template` is can be processed as a signature template.
     */
    public Result check(Element template)
    {
        if (template.getAnnotation(Signature.class) == null) {
            String err = "Tried to check an element as a signature template though it is not "
                       + "annotated with `@Signature`: " + template;
            throw new IllegalArgumentException(err);
        }
        
        // Check to see if we can cast the given element to a type element.
        if (template.getKind() != ElementKind.INTERFACE)
        {
            String err = "A signature template must be an interface, but an element annotated with "
                       + "`@Signature` is of TypeKind {0}.";
            err = format(err, template.getKind());
            return new Error(err, SignatureChecker.class, template);
        }

        for (SignatureCheck check: signatureChecks)
        {
            Result result = check.checkSignature((TypeElement) template);
            if (!result.ok()) {
                return result;
            }
        }

        return ok;
    }
}
