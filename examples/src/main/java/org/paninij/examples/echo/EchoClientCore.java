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

import java.io.*;
import java.net.*;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Root;

@Root
@Capsule
public class EchoClientCore {
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
        CapsuleSystem.start(EchoClient.class, args);
    }
}
