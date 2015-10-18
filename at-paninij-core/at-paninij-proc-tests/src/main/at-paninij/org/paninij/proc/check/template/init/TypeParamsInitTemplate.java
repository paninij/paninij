package org.paninij.proc.check.template.init;

import java.util.List;

import org.paninij.lang.Capsule;
import org.paninij.proc.check.template.BadTemplate;

@BadTemplate
@Capsule
public class TypeParamsInitTemplate
{
    <T> void init(List<T> list) {
        // Nothing
    }
}
