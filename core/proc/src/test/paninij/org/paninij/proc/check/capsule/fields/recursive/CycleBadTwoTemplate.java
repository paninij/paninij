package org.paninij.proc.check.capsule.fields.recursive;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule
public class CycleBadTwoTemplate {
    @Local CycleBadOne one;
}