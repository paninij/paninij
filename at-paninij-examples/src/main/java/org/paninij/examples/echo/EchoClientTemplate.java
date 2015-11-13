
package org.paninij.examples.echo;

import java.io.*;
import java.net.*;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Root;

@Root
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
        CapsuleSystem.start(EchoClient.class, args);
    }
}
