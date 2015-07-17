package edu.rice.habanero.benchmarks.nqueenk;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class WorkerTemplate {
    @Wired Master master;

    int threshold = NQueensConfig.THRESHOLD;
    int size = NQueensConfig.SIZE;

    public void terminate() {
        master.terminate();
    }

    public void nqueensKernalPar(int[] a, int depth) {
        if (size == depth) {
            master.result();
        } else if (depth >= threshold) {
            nqueensKernalSeq(a, depth);
        } else {
            int newDepth = depth + 1;
            for (int i = 0; i < size; i++) {
                int[] b = new int[newDepth];
                System.arraycopy(a, 0, b, 0, depth);
                b[depth] = i;
                if (NQueensConfig.boardValid(newDepth, b)) {
                    master.sendWork(b, newDepth);
                }
            }
        }
        master.workerDone();
    }

    private void nqueensKernalSeq(int[] a, int depth) {
        if (size == depth) {
            master.result();
        } else {
            int[] b = new int[depth + 1];
            for (int i = 0; i < size; i++) {
                System.arraycopy(a, 0, b, 0, depth);
                b[depth] = i;
                if (NQueensConfig.boardValid(depth + 1,  b)) {
                    nqueensKernalSeq(b, depth + 1);
                }
            }
        }
    }

}
