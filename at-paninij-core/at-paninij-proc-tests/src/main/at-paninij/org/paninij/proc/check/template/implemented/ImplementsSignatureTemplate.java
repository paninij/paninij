package org.paninij.proc.check.template.implemented;

import org.paninij.lang.BadTemplate;
import org.paninij.lang.Capsule;
import org.paninij.lang.String;
import org.paninij.proc.helloworld.Stream;

@BadTemplate
@Capsule
public class ImplementsSignatureTemplate implements Stream
{
    @Override
    public void write(String s) {
        // Nothing needed here.
    }
}
