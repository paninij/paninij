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

import org.paninij.soter.LeakyServer;
import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
class ActiveClientCore
{
    @Local LeakyServer server;
    Secret secret;
    Integer integer;
    
    void init()
    {
        secret = new Secret();
        integer = new Integer(42);
    }
    
    void run()
    {
        System.out.println("Starting `ActiveClient`.");
        
        server.giveSecret(secret);          // Unsafe
        server.giveSecret(new Secret());    // Safe

        server.giveInteger(integer);        // Safe (because Integer is transitively immutable)
        server.giveInteger(new Integer(7)); // Safe

        Secret s = server.getSecret();

        System.out.println("Stopping `ActiveClient`.");
    }
    
    public static void main(String[] args) {
        CapsuleSystem.start(ActiveClient.class, args);
    }
}
