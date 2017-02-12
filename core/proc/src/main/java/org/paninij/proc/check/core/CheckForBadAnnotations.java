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
package org.paninij.proc.check.core;

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
 * Check that a core does not have certain bad annotations. For example, capsule and signature
 * cores should not be annotated with {@code @Block}, {@code @Future}, or {@code Duck}.
 */
public class CheckForBadAnnotations implements CoreCheck {

    @Override
    public Result checkCore(TypeElement core, CoreKind kind) {
        String annoName = findProcedureAnnotation(core);
        if (annoName != null) {
            String kindText = kind == CoreKind.CAPSULE ? "capsule" : "signature";
            String err = "A {0} core must not be annotated with `@{1}`.";
            err = format(err, kindText, annoName);
            return error(err, CheckForBadAnnotations.class, core);
        }
        return OK;
    }

    private String findProcedureAnnotation(TypeElement core) {
        if (core.getAnnotation(Block.class) != null) {
            return "Block";
        } else if (core.getAnnotation(Future.class) != null) {
            return "Future";
        } else if (core.getAnnotation(Duck.class) != null) {
            return "Duck";
        } else if (core.getAnnotation(Handler.class) != null) {
            return "Handler";
        } else if (core.getAnnotation(Chain.class) != null) {
            return "Chain";
        } else if (core.getAnnotation(Broadcast.class) != null) {
            return "Broadcast";
        }
        return null;
    }
}
