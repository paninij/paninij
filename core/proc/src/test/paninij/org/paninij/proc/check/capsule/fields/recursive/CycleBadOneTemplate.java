package org.paninij.proc.check.capsule.fields.recursive;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule
public class CycleBadOneTemplate {
    @Local CycleBadTwo;
}