package org.paninij.soter;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class ActiveClientTemplate
{
    @Child LeakyServer server;

    public void run()
    {
        @SuppressWarnings("unused")
        Secret s = server.getSecret();
    }
}
