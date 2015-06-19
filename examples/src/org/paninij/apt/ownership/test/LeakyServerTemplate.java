package org.paninij.apt.ownership.test;

import org.paninij.lang.Capsule;

/**
 * Implements a server which gives away a reference to its state.
 */
@Capsule
public class LeakyServerTemplate
{
    Secret serverSecret = new Secret();
    
    public Integer getInteger() {
        return new Integer(9);
    }

    public void giveInteger(Integer i) {
        // Nothing to do.
    }
    
    public Secret getSecret() {
        return serverSecret;
    }
    
    public LeakyServerTemplate getTemplateReference() {
        return this;
    }
}
