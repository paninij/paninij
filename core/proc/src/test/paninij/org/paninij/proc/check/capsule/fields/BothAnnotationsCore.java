package org.paninij.proc.check.capsule.fields;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Imports;

import org.paninij.ex.helloworld.HelloWorldShort;

@Capsule
public class BothAnnotationsCore
{
    @Local @Imports HelloWorldShort helloWorld;
}
