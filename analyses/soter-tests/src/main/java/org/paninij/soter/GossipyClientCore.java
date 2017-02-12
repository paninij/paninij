/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
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
public class GossipyClientCore
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
        CapsuleSystem.start(GossipyClient.class, args);
    }
}
