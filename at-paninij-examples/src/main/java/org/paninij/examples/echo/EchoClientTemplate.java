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

import java.io.*;
import java.net.*;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;

@Capsule
public class EchoClientTemplate {
    Socket echoSocket;
    PrintWriter out;
    BufferedReader in;

    public void init() {
        echoSocket = null;
        out = null;
        in = null;
    }

    private void open() {
        try {
            echoSocket = new Socket("localhost", 8080);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException e) {
            e.printStackTrace(System.err);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private void close() {
        try {
            out.close();
            in.close();
            echoSocket.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    void run() {
        try {
            open();
            out.println("Hello Server!");
            System.out.println("Server replied: " + in.readLine());
            out.println("" + System.currentTimeMillis() + ".");
            System.out.println("Server replied: " + in.readLine());
            out.println("Good bye.");
            System.out.println("Server replied: " + in.readLine());
            close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
    
    public static void main(String[] args) {
        CapsuleSystem.start(EchoClient.class.getName(), args);
    }
}
