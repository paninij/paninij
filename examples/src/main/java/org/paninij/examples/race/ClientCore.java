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
package org.paninij.examples.race;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class ClientCore
{
    @Local Server server;
    @Local Relay relay;

    void design(Client self) {
        relay.imports(server);
    }

    /**
     * This method illustrates racey behavior.
     * 
     * Since this code triggers `relay` to say hello before the client itself says hello, one
     * might expect the relay to always say Hello first. However, because this is a concurrent
     * system, this is not necessarily the case. The message which actually arrives at the server
     * capsule first depends on how long it takes to do this work.
     *
     * - Less work means that the `Client` is more likely to say hello first.
     * - More work means that the `Relay` is more likely to say hello first.
     * 
     * But in this concurrent system, no matter how large we make the argument to work, there is no
     * absolute guarantee about the order in which the server will receive its messages.
     */
    void run() {
        relay.sayHello();
        work(10);
        server.sayHello("Client");
    }

    @SuppressWarnings("unused")
    private void work(int iter) {
        double y;
        for (int i = 0; i < iter; i++) {
            y = Math.PI;
        }
    }
    
    public static void main(String[] args) {
        CapsuleSystem.start(Client.class, args);
    }
}
