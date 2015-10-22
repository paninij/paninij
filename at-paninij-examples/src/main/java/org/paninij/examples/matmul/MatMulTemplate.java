package org.paninij.examples.matmul;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class MatMulTemplate {
    @Local Master master;
    @Local MatrixData data;

    public void design(MatMul self) {
        master.imports(data);
    }

    public void run() {
        master.start();
    }

    public static void main(String[] args) {
        CapsuleSystem.start(MatMul.class.getName(), args);
    }
}
