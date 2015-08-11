package org.paninij.examples.matmul;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class MatMulTemplate {
    @Child Master master;
    @Child MatrixData data;

    public void design(MatMul self) {
        master.wire(data);
    }

    public void run() {
        master.start();
    }

}
