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
package org.paninij.proc.check;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import org.paninij.proc.PaniniProcessor;
import org.paninij.proc.util.PaniniModel;

public class CapsuleTestChecker
{
    /*
     * TODO: Check that a user-defined capsule tester template is well formed. Some criteria:
     * 
     *  - A tester's name is suffixed with `CAPSULE_TESTER_SUFFIX`.
     *  - A tester must be a class annotated with `@CapsuleTester`.
     *  - A tester has no procedures, only tests. Private methods are allowed.
     *  - A tester does not have a run declaration. (All @Test methods are essentially run decls.)
     *  - All tests take no arguments and return void.
     */
    public static boolean check(PaniniProcessor context, Element tester)
    {
        return (checkElementKind(context, tester)
             && checkName(context, tester));
    }
    
    private static boolean checkElementKind(PaniniProcessor context, Element tester)
    {
        return tester.getKind() == ElementKind.CLASS;
    }
    
    private static boolean checkName(PaniniProcessor context, Element tester)
    {
        // TODO: Add helpful error messages.

        String name = tester.getSimpleName().toString();
        return (name.length() > PaniniModel.CAPSULE_TEST_TEMPLATE_SUFFIX.length()
             && name.endsWith(PaniniModel.CAPSULE_TEST_TEMPLATE_SUFFIX));
    }
}
