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

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;

@Capsule public class MatrixDataCore {

    int length = Config.DATA_LENGTH;

    double[][] A;
    double[][] B;
    double[][] C;

    public void init() {
        A = new double[length][length];
        B = new double[length][length];
        C = new double[length][length];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                A[i][j] = i;
                B[i][j] = j;
            }
        }
    }

    /*
     * Check if `C` is equal to `A` multiplied by `B`
     */
    @Block
    public boolean valid() {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                double actual = C[i][j];
                double expected = 1.0 * length * i * j;
                if (Double.compare(actual,  expected) != 0) return false;
            }
        }
        return true;
    }

    /*
     * Add and set `C[i][j]` to `v`
     */
    public void addC(int i, int j, double v) {
        C[i][j] += v;
    }

    /*
     * Get `A[i][j]`
     */
    @Block
    public double getA(int i, int j) {
        return A[i][j];
    }

    /*
     * Get `B[i][j]`
     */
    @Block
    public double getB(int i, int j) {
        return B[i][j];
    }

    /*
     * Get the dimenion of the matrix
     */
    @Block
    public int dimension() {
        return length;
    }

    public void print() {
        for (int i = 0; i < length; i++) {
            System.out.print("|");
            for (int j = 0; j < length; j++) {
                System.out.print(String.format("%5s", A[j][i]));
            }
            System.out.print("|  *  |");

            for (int j = 0; j < length; j++) {
                System.out.print(String.format("%5s", B[j][i]));
            }
            System.out.print("|  =  |");
            for (int j = 0; j < length; j++) {
                System.out.print(String.format("%5s", C[j][i]));
            }
            System.out.print("|");
            System.out.println("");
        }
        System.out.println("valid: " + valid());
    }

}
