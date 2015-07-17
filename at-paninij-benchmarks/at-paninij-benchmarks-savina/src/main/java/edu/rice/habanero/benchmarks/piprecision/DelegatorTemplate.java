package edu.rice.habanero.benchmarks.piprecision;

import java.math.BigDecimal;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class DelegatorTemplate
{
    @Wired Worker[] workers;

    public BigDecimal pi = BigDecimal.ZERO;
    public final BigDecimal tolerance = BigDecimal.ONE.movePointLeft(PiPrecisionConfig.PRECISION);

    public int numTermsRequested = 0;
    public int numTermsReceived = 0;

    FlagFuture finished = new FlagFuture();
    Result[] results = new Result[20];

    public FlagFuture start() {
        for (int i = 0; i < PiPrecisionConfig.NUM_WORKERS; i++) {
            generateWork(i);
        }
        return finished;
    }

    private void generateWork(int indx) {
        results[indx] = workers[indx].work(numTermsRequested, indx);
        numTermsRequested++;
    }

    public void resultFinished(int indx) {
        numTermsReceived++;
        BigDecimal part = results[indx].getValue();
        pi = pi.add(part);
        if (part.compareTo(tolerance) > 0) {
            generateWork(indx);
            return;
        }
        if (numTermsReceived == numTermsRequested) {
            for (Worker w : workers) w.exit();
            finished.resolve();
        }
    }

}
