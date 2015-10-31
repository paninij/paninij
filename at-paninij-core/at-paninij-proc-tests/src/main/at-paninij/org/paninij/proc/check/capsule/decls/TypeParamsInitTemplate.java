package org.paninij.proc.check.capsule.decls;

import java.util.List;

import org.paninij.lang.BadTemplate;
import org.paninij.lang.Capsule;

@BadTemplate
@Capsule
public class TypeParamsInitTemplate
{
    <T> void init(List<T> list) {
        // Nothing
    }
}
