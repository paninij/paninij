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
package org.paninij.examples.matmul;

public class Work {
    public final int priority;
    public final int srA;
    public final int scA;
    public final int srB;
    public final int scB;
    public final int srC;
    public final int scC;
    public final int numBlocks;
    public final int dim;

    public Work(
            final int priority,
            final int srA, final int scA,
            final int srB, final int scB,
            final int srC, final int scC,
            final int numBlocks, final int dim) {
        this.priority = priority;
        this.srA = srA;
        this.scA = scA;
        this.srB = srB;
        this.scB = scB;
        this.srC = srC;
        this.scC = scC;
        this.numBlocks = numBlocks;
        this.dim = dim;
    }
}
