package org.paninij.soter;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule
public class TwoPathsToTransferTemplate
{
    @Local LeakyServer leakyServer;
    Secret s = new Secret();
    
    public void a() {
        privateSender();
    }
    
    public void b() {
        privateSender();
    }
    
    private void privateSender() {
        leakyServer.giveSecret(s);
    }
    
    public void c() {
        publicSender();
    }
    
    public void d() {
        publicSender();
    }
    
    public void publicSender() {
        leakyServer.giveSecret(s);
    }
}
