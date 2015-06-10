package org.paninij.examples.ownership;

import org.paninij.lang.*;

/**
 * Implements a server which gives away a reference to its state.
 */
@Capsule
public class LeakyServerTemplate
{
    Integer server_secret = 10;

    public void giveSecret(Integer client_secret) {
        System.out.println("Server: Client's secret is " + client_secret);
    }
    
    public Integer getSecret() {
        return server_secret;
    }
    
    public LeakyServerTemplate getTemplateReference() {
        return this;
    }
}
