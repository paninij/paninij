package org.paninij.soter.tests;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class ActiveClientTemplate
{
    @Child LeakyServer server;
    Secret secret = new Secret();
    Integer integer = new Integer(42);
    
    public void run()
    {
        server.giveSecret(secret);          // Unsafe
        server.giveSecret(new Secret());    // Safe

        server.giveInteger(integer);        // Safe (because Integer is transitively immutable)
        server.giveInteger(new Integer(7)); // Safe

        Secret s = server.getSecret();
    }
}
