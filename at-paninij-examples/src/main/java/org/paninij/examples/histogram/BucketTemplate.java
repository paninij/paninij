
package org.paninij.examples.histogram;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

/**
 * Each Bucket holds the count for an ascii character.
 * Buckets also require a Printer capsule.
 */
@Capsule public class BucketTemplate
{
    @Imports Printer p;

    long count;

    void init() {
        count = 0;
    }

    public void bump() {
        count++;
    }

    @Block
    public void finish(int index) {
        p.print("" + index + "(" + (char) index + "):" + count);
    }
}
