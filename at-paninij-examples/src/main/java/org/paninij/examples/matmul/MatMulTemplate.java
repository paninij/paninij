package org.paninij.examples.matmul;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class MatMulTemplate {
    @Local Master master;
    @Local MatrixData data;

    public void design(MatMul self) {
        master.imports(data);
    }

    public void run() {
        master.start();
    }

}
