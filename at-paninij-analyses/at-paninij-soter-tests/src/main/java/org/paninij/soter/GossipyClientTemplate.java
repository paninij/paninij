package org.paninij.soter;

import static org.paninij.soter.Config.*;

import java.util.Random;

import org.paninij.soter.LeakyServer;
import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class GossipyClientTemplate
{
    @Local LeakyServer servers[] = new LeakyServer[NUM_SERVERS];
    @Local LeakyServer importantServer;

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
        System.out.println("Starting `GossipyClient`.");

        Random rand = new Random();
        for (int gossip = 0; gossip < NUM_GOSSIPS; gossip++)
        {
            int serverIdx = rand.nextInt(NUM_SERVERS);
            int secretIdx = rand.nextInt(NUM_SECRETS);
            servers[serverIdx].giveSecret(secrets[secretIdx]);
        }
        
        importantServer.giveSecret(superSecret);

        System.out.println("Stopping `GossipyClient`.");
    }
    
    public static void main(String[] args) {
        CapsuleSystem.start(GossipyClient.class.getName(), args);
    }
}
