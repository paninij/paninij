package org.paninij.examples.matmul;

import org.paninij.lang.Capsule;

@Capsule public class MatrixDataTemplate {

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
    public double getA(int i, int j) {
        return A[i][j];
    }

    /*
     * Get `B[i][j]`
     */
    public double getB(int i, int j) {
        return B[i][j];
    }

    /*
     * Get the dimenion of the matrix
     */
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
