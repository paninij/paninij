
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
public class EchoServerTemplate
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
    public void init() {
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
     * EchoServerTemplate.
     */
    public void design(EchoServer self) {
        for (Worker w : this.workers)
            w.imports(self);
    }

    @Block
    public Socket getConnection() {
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