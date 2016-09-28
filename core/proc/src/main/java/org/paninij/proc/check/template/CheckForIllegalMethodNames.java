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
package org.paninij.proc.check.template;

import static java.text.MessageFormat.format;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

/**
 * Check that a template does not have certain illegal or reserved method names. For example, a
 * capusle or signature template cannot have a method named {@code imports()} or {@code exit()}.
 */
public class CheckForIllegalMethodNames implements TemplateCheck
{
    private static final String[] BAD_METHOD_NAMES = {
        "imports",
        "exit",
    };
    
    @Override
    public Result checkTemplate(TypeElement template, TemplateKind kind) {
        for (Element elem : template.getEnclosedElements()) {
            if (isMethodWithBadName(elem)) {
                String err = "A {0} template cannot declare a method named `{1}()`.";
                err = format(err, kind, elem.getSimpleName());
                return error(err, CheckForIllegalMethodNames.class, elem);
            }
        }
        return OK;
    }
    
    private static boolean isMethodWithBadName(Element elem) {
        return elem.getKind() == ElementKind.METHOD
            && hasBadMethodName(elem);
    }
    
    private static boolean hasBadMethodName(Element elem) {
        String name = elem.getSimpleName().toString();
        for (String badName : BAD_METHOD_NAMES) {
            if (name.equals(badName)) {
                return true;
            }
        }
        return false;
    }
}
