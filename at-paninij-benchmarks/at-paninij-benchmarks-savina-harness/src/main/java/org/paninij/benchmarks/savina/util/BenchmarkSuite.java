package org.paninij.benchmarks.savina.util;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import scala.actors.threadpool.Arrays;


public class BenchmarkSuite {
    static String OUTFOLDER = "C:/Users/dmill_000/Desktop/BENCHMARK_RESULTS/raw/";

    public static void mark(String name) {
        try {
            System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(OUTFOLDER + name + "-raw.txt")), true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
