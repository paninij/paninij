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

import static org.paninij.proc.check.Result.ok;

import static java.text.MessageFormat.format;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;
import org.paninij.proc.util.PaniniModel;


public class SuffixCheck extends AbstractTemplateCheck
{
    @Override
    protected Result checkTemplate(TemplateKind kind, TypeElement template) {
        String expectedSuffix = getExpectedSuffix(kind);
        String name = template.getSimpleName().toString();
        String err = null;

        if (!name.endsWith(expectedSuffix)) {
            err = ("A {0} template name must be suffixed with `{1}`");
            err = format(err, kind.toString(), expectedSuffix);
        } else if (name.length() == expectedSuffix.length()) {
            err = "A {0} template name must not be the same as the expected suffix, `{1}`";
            err = format(err, kind.toString(), expectedSuffix);
        }

        return (err == null) ? ok : new Error(err, SuffixCheck.class, template);
    }
    
    private static String getExpectedSuffix(TemplateKind kind) {
        switch (kind) {
        case CAPSULE:
            return PaniniModel.CAPSULE_TEMPLATE_SUFFIX;
        case SIGNATURE:
            return PaniniModel.SIGNATURE_TEMPLATE_SUFFIX;
        default:
            throw new IllegalArgumentException("Unknown template kind: " + kind);
        }
    }
}
