

/***
 * Classic Histogram problem using the Panini language
 */
package org.paninij.examples.histogram;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class HistogramTemplate
{
    @Local Printer printer;
    @Local Reader reader;
    @Local Bucket[] buckets = new Bucket[128];

    String[] filenames;

    public void init() {
        filenames = new String[1];
        filenames[0] = "shaks12.txt";
    }

    public void design(Histogram self) {
        reader.imports(buckets);
        for (Bucket bucket : buckets) bucket.imports(printer);
    }

    public void run() {
        reader.read(filenames);
    }
    
    public static void main(String[] args) {
        CapsuleSystem.start(Histogram.class, args);
    }
}
