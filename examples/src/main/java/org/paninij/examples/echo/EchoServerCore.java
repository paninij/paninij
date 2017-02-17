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

package org.paninij.examples.echo;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Local;

import java.io.IOException;
import java.net.*;
import org.paninij.lang.Root;

@Root
@Capsule
class EchoServerCore
{
    // An EchoServer will have 10 Worker capsules
    // The @Child annotation will automatically
    // instantiate and populate the Array of workers.
    // The size of the array must be provided at this
    // point.
    @Local Worker[] workers = new Worker[10];

    // @Child Worker[] workers; - This will fail because it
    // does not provide the size of the array.

    ServerSocket ss;

    /*
     * The init() method is a special declaration, similar to a
     * constructor.
     */
    void init() {
        try {
            ss = new ServerSocket(8080);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /*
     * The design() method is a special declaration used to
     * describe how capsules are connected.
     *
     * In this example, all of the Worker capsules will get
     * a reference to an EchoServer. Notice how the first
     * argument is an EchoServer called "self". Self is a
     * an instance of a capsule described by this
     * EchoServerCore.
     */
    void design(EchoServer self) {
        for (Worker w : this.workers)
            w.imports(self);
    }

    @Block
    Socket getConnection() {
        Socket s = null;
        try {
            // a blocking call which waits for a client to
            // connect
            s = ss.accept();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return s;
    }

    public static void main(String[] args) {
        CapsuleSystem.start(EchoServer.class, args);
    }
}
