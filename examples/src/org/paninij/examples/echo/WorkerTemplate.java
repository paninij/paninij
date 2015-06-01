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

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

import java.net.*;
import java.io.*;

@Capsule
public class WorkerTemplate {

    @Wired EchoServer l;

    public void run() {
        while (true) {
            Socket s = l.getConnection();
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
        } catch (IOException e) { e.printStackTrace(System.err); }
    }
}