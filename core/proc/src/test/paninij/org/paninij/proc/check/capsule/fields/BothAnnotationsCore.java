package org.paninij.proc.check.capsule.fields;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Imported;

import org.paninij.ex.helloworld.HelloWorldShort;

@Capsule
class BothAnnotationsCore
{
    @Local @Imported HelloWorldShort helloWorld;
}
