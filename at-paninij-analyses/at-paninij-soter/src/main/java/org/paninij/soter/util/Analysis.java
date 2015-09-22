package org.paninij.soter.util;

public abstract class Analysis implements IdempotentOperation
{
    protected boolean hasBeenPerformed = false;

    /**
     * If this method has never been called before, then any sub-analyses and the main analysis are
     * performed. If this method has been previously called, then it will return immediately.
     * 
     * Warning: implementers of `Analysis` should not not generally override `perform()`. They are
     * expected to usually override `performAnalysis()` and `performSubAnalyses()`.
     */
    public void perform()
    {
        if (hasBeenPerformed) {
            return;
        }
        performSubAnalyses();
        performAnalysis();
        hasBeenPerformed = true;
        assert checkPostConditions();
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
        // By default, assume that there are no sub-analyses. Do nothing.
    }
    
    public boolean hasBeenPerformed() {
        return hasBeenPerformed;
    }

    /**
     * If there are any conditions that should be checked after performing the analysis, they can be
     * put in a method which overrides this one. This method is automatically called after
     * performing an analysis when assertions are enabled. This is only meant to be used for
     * debugging purposes.
     * 
     * @return `true` iff all post conditions are satisfied.
     */
    protected boolean checkPostConditions()
    {
        // By default, assume that there are no post-conditions. Do nothing.
        return true;
    }
}
