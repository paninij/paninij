package org.paninij.apt.test;

import org.paninij.apt.util.Source;

public class SourceHelperTester
{

    public static void main(String[] args)
    {
        String testFmt = Source.lines(1, "##");
        
        String aligned = Source.formatAligned(testFmt, "line1", "line2", "line3");
        
        System.out.println(aligned);

    }

}
