package org.paninij.proc.maingen;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule
public class ActiveLocalTemplate {

    @Imports PassiveLocal p;

    public void run() {
        System.out.println("Hello world");
    }
}
