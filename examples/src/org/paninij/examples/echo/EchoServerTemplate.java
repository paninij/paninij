/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills, Hridesh Rajan, Steven M. Kautz
 */
package org.paninij.examples.echo;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

import java.io.IOException;
import java.net.*;

@Capsule
public class EchoServerTemplate {
    // TODO: Change this to an array of workers.
    @Child Worker w;

    ServerSocket ss;

    public void init() {
        try {
            ss = new ServerSocket(8080);
        } catch (IOException e) { e.printStackTrace(System.err); }
    }

    public void design(EchoServer self) {
        w.wire(self);
    }

    @Block
    public Socket getConnection() {
        Socket s = null;
        try {
            s = ss.accept();
        } catch (IOException e) { e.printStackTrace(System.err); }
        return s;
    }

    // TODO: Currently the run() method is required here so the annotation processor
    // will automatically generate a main() method for it.
    public void run() {
        //do nothing
    }
}