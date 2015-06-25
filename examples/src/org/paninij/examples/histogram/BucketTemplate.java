package org.paninij.examples.histogram;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class BucketTemplate
{
    @Wired Printer p;

    long count;

    void init() {
        count = 0;
    }

    void bump() {
        count++;
    }

    void finish(int index) {
        p.print("" + index + "(" + (char) index + "):" + count);
    }
}
