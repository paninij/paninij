package edu.rice.habanero.benchmarks.piprecision;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Queue;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;
import org.paninij.lang.Wired;

import edu.rice.habanero.benchmarks.pingpong.FlagFuture;

@Capsule public class DelegatorTemplate
{
    @Wired Worker[] workers;

    public BigDecimal pi = new BigDecimal(0);
    public final BigDecimal tolerance = BigDecimal.ONE.movePointLeft(PiPrecisionConfig.PRECISION);

    public int numTermsRequested = 0;
    public int numTermsReceived = 0;

    FlagFuture finished = new FlagFuture();
    Result[] results = new Result[20];

    public FlagFuture start() {
        for (int i = 0; i < PiPrecisionConfig.NUM_WORKERS; i++) {
            generateWork(workers[i], i);
        }
        return finished;
    }

    private void generateWork(Worker w, int indx) {
        numTermsRequested++;
        results[indx] = w.work(PiPrecisionConfig.PRECISION, numTermsRequested, indx);
    }

    public void resultFinished(int indx) {
        numTermsReceived++;
        Result r = results[indx];
        BigDecimal dec = r.getResult();
        pi = pi.add(dec);
        System.out.println("Recieved a result: " + dec);
        System.out.println("Cur pi: " + pi);
        if (dec.compareTo(tolerance) > 0) {
            generateWork(workers[indx], indx);
            return;
        }
        if (numTermsReceived == numTermsRequested) {
            System.out.println("RESOLVING");
            finished.resolve();
        }
    }

}
