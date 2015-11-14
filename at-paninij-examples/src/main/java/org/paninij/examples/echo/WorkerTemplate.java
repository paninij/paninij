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

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

import java.net.*;
import java.io.*;

@Capsule
public class WorkerTemplate {

    // A 'Worker' capsule will have a reference to an EchoServer capsule. The
    // reference will be provided by it's parent capsule (in the design()
    // declaration). In this case, the parent is also an EchoServer.
    @Imports EchoServer server;

    public void run() {
        while (true) {
            Socket s = server.getConnection();
            handleConnection(s);
        }
    }

    private void handleConnection(Socket s) {
        try {
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String clientInput;
            while ((clientInput = in.readLine()) != null) {
                System.out.println("client says: " + clientInput);
                out.println(clientInput);
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
