
package org.paninij.examples.histogram;

import org.paninij.lang.Capsule;

/**
 * Printer capsule contains one procedure: print(String). It's job is
 * to output (print) the given String.
 */
@Capsule public class PrinterTemplate
{
    public void print(String output) {
        System.out.println(output);
    }
}
