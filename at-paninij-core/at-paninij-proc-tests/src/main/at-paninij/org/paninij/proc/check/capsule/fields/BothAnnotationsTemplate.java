package org.paninij.proc.check.capsule.fields;

import org.paninij.lang.BadTemplate;
import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Imports;

import org.paninij.proc.helloworld.HelloWorld;

@BadTemplate
@Capsule
public class BothAnnotationsTemplate
{
    @Local @Imports HelloWorld helloWorld;
}
