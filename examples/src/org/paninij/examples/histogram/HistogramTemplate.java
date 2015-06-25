package org.paninij.examples.histogram;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class HistogramTemplate
{
    @Child Printer printer;
    @Child Reader reader;
    @Child Bucket[] buckets = new Bucket[128];

    String[] filenames;

    public void init() {
        filenames = new String[1];
        filenames[0] = "shaks12.txt";
    }

    public void design(Histogram self) {
        reader.wire(buckets);
        for (Bucket bucket : buckets) bucket.wire(printer);
    }

    public void run() {
        reader.read(filenames);
    }
}
