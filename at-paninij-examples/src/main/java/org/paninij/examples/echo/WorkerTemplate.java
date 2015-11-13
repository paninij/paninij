
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
