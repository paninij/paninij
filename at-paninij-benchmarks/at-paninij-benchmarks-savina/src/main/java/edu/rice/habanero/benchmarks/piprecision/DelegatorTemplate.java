package edu.rice.habanero.benchmarks.piprecision;

import java.math.BigDecimal;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class DelegatorTemplate
{
    @Local Worker[] workers = new Worker[PiPrecisionConfig.NUM_WORKERS];

    public BigDecimal pi = BigDecimal.ZERO;
    public final BigDecimal tolerance = BigDecimal.ONE.movePointLeft(PiPrecisionConfig.PRECISION);

    public int numTermsRequested = 0;
    public int numTermsReceived = 0;

    public void design(Delegator self) {
        for (int i = 0; i < workers.length; i++) {
            workers[i].imports(self, i);
        }
    }

    public void start() {
        for (int i = 0; i < PiPrecisionConfig.NUM_WORKERS; i++) {
            generateWork(i);
        }
    }

    private void generateWork(int indx) {
        numTermsRequested++;
        workers[indx].work(numTermsRequested);
    }

    public void resultFinished(BigDecimal result, int indx) {
        numTermsReceived++;
        pi = pi.add(result);
        if (result.compareTo(tolerance) > 0) {
            generateWork(indx);
            return;
        }
        if (numTermsReceived == numTermsRequested) {
            for (Worker w : workers) w.exit();
        }
    }

}
