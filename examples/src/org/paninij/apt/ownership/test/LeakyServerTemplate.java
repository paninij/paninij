package org.paninij.apt.ownership.test;

import org.paninij.lang.*;

/**
 * Implements a server which gives away a reference to its state.
 */
@Capsule
public class LeakyServerTemplate
{
    Integer serverSecret = 10;
    
    public Integer getInteger() {
        return new Integer(9);
    }

    public void giveInteger(Integer i) {
        System.err.println("Server: received Integer " + i);
    }
    
    public Integer getSecret() {
        return serverSecret;
    }
    
    public LeakyServerTemplate getTemplateReference() {
        return this;
    }
}
