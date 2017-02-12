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
package org.paninij.proc.check.core;

import static org.paninij.proc.check.Check.Result.OK;

import static java.text.MessageFormat.format;
import static org.paninij.proc.check.Check.Result.error;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.util.PaniniModel;


public class CheckSuffix implements CoreCheck
{
    @Override
    public Result checkCore(TypeElement core, CoreKind kind) {
        String expectedSuffix = getExpectedSuffix(kind);
        String name = core.getSimpleName().toString();
        String err = null;

        if (!name.endsWith(expectedSuffix)) {
            err = ("A {0} core name must be suffixed with `{1}`");
            err = format(err, kind.toString(), expectedSuffix);
        } else if (name.length() == expectedSuffix.length()) {
            err = "A {0} core name must not be the same as the expected suffix, `{1}`";
            err = format(err, kind.toString(), expectedSuffix);
        }

        return (err == null) ? OK : error(err, CheckSuffix.class, core);
    }
    
    private static String getExpectedSuffix(CoreKind kind) {
        switch (kind) {
        case CAPSULE:
            return PaniniModel.CAPSULE_CORE_SUFFIX;
        case SIGNATURE:
            return PaniniModel.SIGNATURE_SPEC_SUFFIX;
        default:
            throw new IllegalArgumentException("Unknown core kind: " + kind);
        }
    }
}
