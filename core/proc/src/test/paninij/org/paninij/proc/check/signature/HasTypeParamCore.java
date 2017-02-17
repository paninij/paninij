package org.paninij.proc.check.signature;

import org.paninij.lang.Signature;

@Signature
interface HasTypeParamCore<T>
{
    T proc();
}
