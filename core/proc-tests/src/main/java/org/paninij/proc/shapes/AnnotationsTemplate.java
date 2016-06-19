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
package org.paninij.proc.shapes;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Future;


@Capsule
public class AnnotationsTemplate {

    public void unannotatedVoid() {

    }

    @Block
    public void atBlockVoid() {

    }

    @Future
    public void atFutureVoid() {

    }

    @Block
    public String atBlockFinal() {
        return "I'm blocked";
    }

    @Future
    public String atFutureFinal() {
        return "I'm a future";
    }

    @Future
    public String atFutureFinalArgs(int some, boolean other) {
        return "Hello World!";
    }

    @Future
    public String atBlockFinalArgs(int some, boolean other) {
        return "Hello World!";
    }

    @Future
    public boolean atFuturePrimitive() {
        return true;
    }

    @Block
    public boolean atBlockPrimitive() {
        return true;
    }


    public Object unannotatedObject() {
        return new Object();
    }

    @Block
    public Object atBlockObject() {
        return new Object();
    }

    @Future
    public Object atFutureObject() {
        return new Object();
    }
}