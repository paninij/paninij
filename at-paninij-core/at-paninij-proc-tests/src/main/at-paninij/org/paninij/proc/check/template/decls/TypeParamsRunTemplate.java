package org.paninij.proc.check.template.decls;

import java.util.List;

import org.paninij.lang.Capsule;
import org.paninij.proc.check.template.BadTemplate;

@BadTemplate
@Capsule
public class TypeParamsRunTemplate
{
    <T> void init(List<T> list) {
        // Nothing
    }
}
