package org.paninij.examples.ownership;

import org.paninij.lang.*;

@Capsule
public class LeakyClientTemplate
{
    @Child LeakyServer leaky_server;
    Integer client_secret = 42;
    
    void run()
    {
        leaky_server.giveSecret(client_secret);

        Integer server_secret = leaky_server.getSecret();
        System.out.println("Client: server's secret is " + server_secret);
    }
}
