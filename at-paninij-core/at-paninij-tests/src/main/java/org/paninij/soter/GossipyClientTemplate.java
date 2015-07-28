package org.paninij.soter;

import java.util.Random;

import org.paninij.soter.LeakyServer;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class GossipyClientTemplate
{
    private static final int NUM_GOSSIPS = 17;
    private static final int NUM_SERVERS = 10;
    private static final int NUM_SECRETS = 42;


    @Child LeakyServer servers[] = new LeakyServer[NUM_SERVERS];
    @Child LeakyServer importantServer;

    Secret secrets[] = new Secret[NUM_SECRETS];
    Secret superSecret;

    
    public void init()
    {
        for (int idx = 0; idx < NUM_SECRETS; idx++) {
            secrets[idx] = new Secret();
        }
        superSecret = new Secret();
    }
    

    public void run()
    {
        Random rand = new Random();
        for (int gossip = 0; gossip < NUM_GOSSIPS; gossip++)
        {
            int serverIdx = rand.nextInt(NUM_SERVERS);
            int secretIdx = rand.nextInt(NUM_SECRETS);
            servers[serverIdx].giveSecret(secrets[secretIdx]);
        }
        
        importantServer.giveSecret(superSecret);
    }
}
