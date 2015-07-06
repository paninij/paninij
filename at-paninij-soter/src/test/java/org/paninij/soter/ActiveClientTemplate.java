package org.paninij.soter;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class ActiveClientTemplate
{
    @Child LeakyServer server;
    Secret secret;
    Integer integer;

    public void run()
    {
        @SuppressWarnings("unused")
        Secret s = server.getSecret();      // Unsafe
        server.giveInteger(integer);        // Safe
        server.giveInteger(new Integer(7)); // Safe
        server.giveSecret(secret);          // Unsafe
    }
}
