/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/

package org.paninij.proc.check.capsule;

import static org.paninij.proc.check.Result.ok;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.paninij.lang.Capsule;
import org.paninij.proc.check.Check;
import org.paninij.proc.check.CheckEnvironment;
import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;
import org.paninij.proc.check.template.NoBadMethodNamesCheck;
import org.paninij.proc.check.template.NoDefaultPackageCheck;
import org.paninij.proc.check.template.NoNestedTypesCheck;
import org.paninij.proc.check.template.NoTypeParamCheck;
import org.paninij.proc.check.template.NotSubclassCheck;
import org.paninij.proc.check.template.ProcReturnTypesDuckabilityCheck;
import org.paninij.proc.check.template.SuffixCheck;


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
            new NoDefaultPackageCheck(),
            new NotSubclassCheck(env),
            new NoVarargsMethodsCheck(),
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
            String err = "A capsule template must be a class.";
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
