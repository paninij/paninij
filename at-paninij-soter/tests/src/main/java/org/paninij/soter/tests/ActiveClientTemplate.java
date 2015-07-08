package org.paninij.soter.tests;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class ActiveClientTemplate
{
    @Child LeakyServer server = new LeakyServer$Dummy();
    Secret secret = new Secret();
    Integer integer = new Integer(42);
    
    public void run()
    {
        // Try sending message via procedure invocation:
        server.giveSecret(secret);          // Unsafe
        server.giveInteger(integer);        // Safe
        server.giveInteger(new Integer(7)); // Safe

        // Try making server sent messages via duck resolution:
        @SuppressWarnings("unused")
        Secret s = server.getSecret();      // Unsafe
    }
}
