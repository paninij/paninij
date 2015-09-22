package org.paninij.soter.util;

public abstract class Command implements IdempotentOperation
{
    protected boolean hasBeenPerformed = false;

    
    @Override
    public boolean hasBeenPerformed() {
        return hasBeenPerformed;
    }


    /**
     * If this method has never been called before, then the command will be performed. If this
     * method has been previously called, then it will return immediately.
     * 
     * Warning: implementers of `Command` should not not generally override `perform()`. They are
     * expected to usually override `performCommand()`.
     */
    @Override
    public void perform()
    {
        if (hasBeenPerformed) {
            return;
        }
        try {
            performCommand();
        } catch (Exception ex) {
            throw new RuntimeException("An exception occurred while running a command: " + ex, ex);
        }
        hasBeenPerformed = true;
        assert checkPostConditions();
    }

     /**
     * Calling this performs the command which a `Command` instance provides. After this is
     * called, all of the command's results should have been performed. Note that implementations of
     * this method are not expected to be idempotent, because the `perform()` wrapper is provided to
     * provide idempotency checking.
     */
    protected abstract void performCommand() throws Exception;


    /**
     * If there are any conditions that should be checked after performing the command, they can be
     * put in a method which overrides this one. This method is automatically called after
     * performing an analysis when assertions are enabled. This is only meant to be used for
     * debugging purposes.
     * 
     * @return `true` iff all checked post-conditions are satisfied.
     */
    protected boolean checkPostConditions()
    {
        // By default, assume that there are no post-conditions. Do nothing.
        return true;
    }
}
