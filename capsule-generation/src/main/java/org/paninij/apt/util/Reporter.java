package org.paninij.apt.util;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class Reporter
{
    private Messager messager;

    public Reporter(Messager m) {
        this.messager = m;
    }

    public void error(String m) {
        this.messager.printMessage(Diagnostic.Kind.ERROR, m);
    }

    public void note(String m) {
        this.messager.printMessage(Diagnostic.Kind.NOTE, m);
    }
}
