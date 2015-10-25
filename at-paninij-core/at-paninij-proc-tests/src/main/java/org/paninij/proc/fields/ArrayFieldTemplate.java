package org.paninij.proc.fields;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

import org.paninij.proc.helloworld.HelloWorld;

@Capsule
public class ArrayFieldTemplate
{
    Object[] objects;

    @Local Foo[] fooArr;
}
