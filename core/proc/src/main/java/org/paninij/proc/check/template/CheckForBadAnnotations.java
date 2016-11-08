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
 *  Dr. Hridesh Rajan,
 *  Dalton Mills,
 *  David Johnston,
 *  Trey Erenberger
 *  Jackson Maddox
 *******************************************************************************/
package org.paninij.proc.check.template;

import static org.paninij.proc.check.Check.Result.OK;

import static java.text.MessageFormat.format;
import static org.paninij.proc.check.Check.Result.error;

import javax.lang.model.element.TypeElement;

import org.paninij.lang.Block;
import org.paninij.lang.Broadcast;
import org.paninij.lang.Chain;
import org.paninij.lang.Duck;
import org.paninij.lang.Future;
import org.paninij.lang.Handler;

/**
 * Check that a template does not have certain bad annotations. For example, capsule and signature
 * templates should not be annotated with {@code @Block}, {@code @Future}, or {@code Duck}.
 */
public class CheckForBadAnnotations implements TemplateCheck {

    @Override
    public Result checkTemplate(TypeElement template, TemplateKind kind) {
        String annoName = findProcedureAnnotation(template);
        if (annoName != null) {
            String kindText = kind == TemplateKind.CAPSULE ? "capsule" : "signature";
            String err = "A {0} template must not be annotated with `@{1}`.";
            err = format(err, kindText, annoName);
            return error(err, CheckForBadAnnotations.class, template);
        }
        return OK;
    }

    private String findProcedureAnnotation(TypeElement template) {
        if (template.getAnnotation(Block.class) != null) {
            return "Block";
        } else if (template.getAnnotation(Future.class) != null) {
            return "Future";
        } else if (template.getAnnotation(Duck.class) != null) {
            return "Duck";
        } else if (template.getAnnotation(Handler.class) != null) {
            return "Handler";
        } else if (template.getAnnotation(Chain.class) != null) {
            return "Chain";
        } else if (template.getAnnotation(Broadcast.class) != null) {
            return "Broadcast";
        }
        return null;
    }
}
