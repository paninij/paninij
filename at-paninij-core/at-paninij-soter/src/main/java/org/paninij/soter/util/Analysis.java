package org.paninij.soter.util;

public interface Analysis
{
    /**
     * Calling this performs the analysis. Note that implementations are expected to be idempotent,
     * that is, after the first time it should have no effect. Also, if the implementer depends upon
     * some other `Analysis`, it should be expected that this function will at some point call the
     * `perform()` method of that `Analysis`.
     */
    public void perform();
}
