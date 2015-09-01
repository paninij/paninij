package org.paninij.examples.histogram;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class ReaderTemplate
{
    @Imports Bucket[] buckets;

    public void read(String[] filenames) {
        if (filenames.length == 0) process("shaks12.txt");

        for (String filename : filenames) {
            process(filename);
        }
    }

    private void process(String filename) {
        try {
            FileInputStream stream = new FileInputStream(new File(filename));
            System.out.println("READER: input file " + filename + " successfully opened. Starting processing...");
            int r;
            while ((r = stream.read()) != -1) {
                buckets[(char) r].bump();
            }
            System.out.println("READER: reading complete. Asking buckets to print count.");
        } catch (IOException e) {
            System.out.println(e);
        }

        for (int i = 0; i < buckets.length; i++) buckets[i].finish(i);

        System.out.println("READER: work complete.");

    }
}
