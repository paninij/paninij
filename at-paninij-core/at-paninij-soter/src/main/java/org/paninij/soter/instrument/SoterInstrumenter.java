package org.paninij.soter.instrument;

import org.paninij.soter.SoterAnalysis;
import org.paninij.soter.model.CapsuleTemplate;

import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;

public class SoterInstrumenter
{
    protected final CapsuleTemplate template;
    protected final SoterAnalysis sa;
    protected final String outputPath;
    protected final ClassInstrumenter instrumenter;

    public SoterInstrumenter(CapsuleTemplate template, String outputPath, SoterAnalysis sa,
                             ClassInstrumenter instrumenter)
    {
        this.template = template;
        this.sa = sa;
        this.outputPath = outputPath;
        this.instrumenter = instrumenter;
    }

    public void perform()
    {
        throw new UnsupportedOperationException("TODO");
    }
}
