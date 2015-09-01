package edu.rice.habanero.benchmarks.filterbank;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Imports;

@Capsule public class BankTemplate implements ProcessorTemplate {

    @Imports int sourceId;
    @Imports int numColumns;
    @Imports double[] H;
    @Imports double[] F;
    @Imports Integrator integrator;

    @Local Delay delay1;
    @Local FirFilter fir1;
    @Local SampleFilter sample;
    @Local Delay delay2;
    @Local FirFilter fir2;
    @Local TaggedForward tag;

    public void design(Bank self) {
        delay1.imports(fir1, sourceId + ".1", numColumns - 1);
        fir1.imports(sample, sourceId + ".1", numColumns, H);
        sample.imports(delay2, numColumns);
        delay2.imports(fir2, sourceId + ".2", numColumns -1);
        fir2.imports(tag, sourceId + ".2", numColumns, F);
        tag.imports(integrator, sourceId);
    }

    @Override
    public void process(double value) {
        delay1.process(value);
    }
}
