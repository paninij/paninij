package org.paninij.soter.util;

public abstract class Analysis
{
    protected boolean hasBeenPerformed = false;

    /**
     * If this method has never been called before, then any sub-analyses and the main analysis are
     * performed. If this method has been previously called, then it will return immediately.
     */
    public void perform()
    {
        if (hasBeenPerformed) {
            return;
        }
        performSubAnalyses();
        performAnalysis();
        hasBeenPerformed = true;
    }

     /**
     * Calling this performs the main analysis which an analysis class provides. After this is
     * called, the major results should have been generated. Note that implementations of this
     * method are not expected to be idempotent, because the `perform()` wrapper is provided to
     * provide idempotency checking.
     */
    protected abstract void performAnalysis();

    /**
     * If the main analysis depends any sub-analyses being performed before it can be performed,
     * then those calls should happen here.
     */
    protected void performSubAnalyses()
    {
        // By default, assume that there are no sub-analyses, and do nothing.
    }
}
