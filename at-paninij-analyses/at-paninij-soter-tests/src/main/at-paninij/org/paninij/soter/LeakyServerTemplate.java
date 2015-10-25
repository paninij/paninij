package org.paninij.soter;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;

@Capsule
public class LeakyServerTemplate
{
    Secret serverSecret = new Secret();
    
    @Block
    public Integer getInteger() {
        return new Integer(9);
    }

    public void giveInteger(Integer i) {
        // Nothing to do.
    }
    
    public void giveSecret(Secret s) {
        // Nothing to do.
    }
 
    public Secret getSecret() {
        return serverSecret;
    }
    
    @Block
    public LeakyServerTemplate getTemplateReference() {
        return this;
    }
}
