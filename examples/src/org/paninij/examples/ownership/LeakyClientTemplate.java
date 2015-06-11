package org.paninij.examples.ownership;

import org.paninij.lang.*;
import org.paninij.lang.String;

@Capsule
public class LeakyClientTemplate
{
    @Child LeakyServer leaky_server;
    Integer client_secret = 42;
    
    void run()
    {
        System.out.println(new String("Attempting safe ownership transfer..."));
        leaky_server.giveSecret(new Integer(10));
        
        //System.out.println(new String("Attempting to leak client's secret to server..."));
        //leaky_server.giveSecret(client_secret);

        //System.out.println(new String("Attempting to make server leak a reference to its template instance..."));
        //LeakyServerTemplate leaked_template = leaky_server.getTemplateReference();

        //System.out.println(new String("Attempting to make server leak a reference to its secret..."));
        //Integer leaked_secret = leaky_server.getSecret();
    }
}