---
part: 3
chapter: 10
title: Leader-Follower Pattern
---

## Creating Server/Client applications in Panini

Servers are naturally concurrent applications, they have to be able to respond
to requests from clients at any point in time and can make no assumptions about
when these requests arrive. Panini’s features allow the programmer to write a
server application as if he/she were writting a sequential program and get the
concurrency needed to make the application viable in a real world setting via
implicit concurrency. To illustrate we will be coding a simple EchoServer that
simply repeats everything the clients say.

## Architecture and design

1.  Divide the problems into subproblems. In our case, the subproblems are:
    a. accept incoming connections.
    b. handle these connections accordingly.
    c. for illustration purposes write a client program to help test the server.

2.  The Panini programmer specifies a system as a collection of capsules,
    signatures and ordinary object-oriented classes. A capsule is an abstraction
    for decomposing a software into its parts and a design block is a mechanism
    for composing these parts together. So the first order of business is to
    come up with this capsule-oriented design. This involves creating capsules
    and assigning subtasks to these capsules.

3.  Make key design decisions. In our case, we want our server to be able to
    respons to multiple quasi-simultaneous incoming requests with ease.

4.  Create capsules and assign responsibilities to capsules. We will start by
    defining the capsule EchoServer.

    **Listing 10.1:** Capsule EchoServer
    ``` java
    capsule EchoServer {...}
    ```

    Now that we have a simple name to refer to the server we will define the
    capsule ConnectionHandler which needs to communicate with the server.

    **Listing 10.2:** *Capsule ConnectionHandler*
    ``` java
    capsule ConnectionHandler(EchoServer server) {...}
    ```

    As a separate program we define the client. As you can see bellow the
    EchoClient does not need to know about the capsule EchoServer, it will
    communicate with it via a custom network protocol that will be evident
    later.


    **Listing 10.3:** *The client*
    ``` java
    capsule EchoClient() {...}
    ```

    integrate capsules to form a design block. Since we want our server to
    handle multiple connections at the same time it makes sense to have multiple
    such handlers.

    **Listing 10.4:** *Design block*
    ``` java
    capsule EchoServer() {
      design {
        ConnectionHandler connHandlers[10];
        wireall(connHandlers, this);
      }
    }
    ```

    Every capsule can have a design block, it effectively marks the capsule as a
    high level component that is composed out of other capsules. In our case,
    the best choice would be to give the Pipeline capsule such a block.

    This declarative design block (lines 3-5) declares a set of 10
    ConnectionHandler capsules. On line 4 we link all of these capsules to the
    current EchoServer capsule.

    Since the EchoClient program is composed only out of that one capsule it
    does not require a design block.

## Implementation

### Capsule EchoServer

The only thing that the server does is listen on port 8080 and accept a
connection (line 19), when requested.

**Listing 10.5:** *Entire implementation of EchoServer*
``` java
capsule EchoServer() {
  design {
    ConnectionHandler connHandlers[10];
    wireall(connHandlers, this);
  }
  ServerSocket ss;
  => {
    try {
      ss = new ServerSocket(8080);
    } catch (IOException e){ e.printStackTrace(System.err); }
  }
  Socket getConnection() {
    Socket s = null;
    try {
      s = ss.accept();
    } catch (IOException e){ e.printStackTrace(System.err); }
    return s;
  }
}
```

To allow other capsules to change its state, a capsule can provide capsule
procedures, procedures for short. A capsule procedure is syntactically similar
to methods in object-oriented languages, however, they are different
semantically in two ways: first, a capsule procedures is by default public
(although private helper procedures can be declared using the private keyword),
and second a procedure invocation is guaranteed to be logically synchronous. In
some cases, Panini may be able to run procedures in parallel to improve
parallelism in Panini programs. Calls to a non-void external capsule procedure
immediately returns a "future" value, while the procedure that is called runs
concurrently. That value behaves exactly like normal values, so you won’t need
to modify your programs to make adjustments for it. When you need the actual
value, and if the called procedure has completed running execution proceeds as
usual, otherwise execution is blocked until the called procedure completes
running.

The => denotes a capsule initialization block. Every capsule may declare it. The
semantics of the language ensure that it will be run before the capsule responds
to any messages (procedure calls).

ConnectionHandler When a ConnectionHandler calls the getConnection procedure,
the call returns immediately with a future representing the Socket object, and a
task corresponding to the procedure body is queued for execution in the Host.
When a worker attempts to use the socket in its handleConnection helper, it
blocks until the Server provides the actual socket. A server that can handle
variable size connections can also be implemented similarly by introducing a
mediator capsule between Server and Worker.

**Listing 10.6:** *ConnectionHandler implementation*
``` java
capsule ConnectionHandler(EchoServer server) {
  void run() {
    while (true) {
      Socket s = server.getConnection();
      handleConnection(s);
    }
  }
  void handleConnection(Socket s) {
    try {
      PrintWriter out = new PrintWriter(s.getOutputStream(), true);
      BufferedReader in = new BufferedReader(
                 new InputStreamReader(s.getInputStream()));
      String clientInput;
      while ((clientInput = in.readLine()) != null) {
        System.out.println("client␣says:␣" + clientInput);
        out.println(clientInput);
      }
    } catch (IOException e) { e.printStackTrace(System.err); }
  }
}
```

Any capsule with a run procedure begins executing independently as soon as the
initialization and interconnection of all capsules is complete and may generate
calls to the procedures of other capsules. For example, capsule Pipeline will
run the code on lines 3-8. Capsules without a run procedure, such as EchoServer,
perform computation only when their procedures are invoked.

The implementation of the handleConnection procedures should be easily
understood by any Java programmer familiar with network communication. For any
given connection, the handler will simply read all input and print it to the
standard output.

Capsule EchoClient. A client will simply open a connection to a running server
through port 8080, send a "Hello Server!" and "Goodbye Server!" message, both
times printing the server’s response.

**Listing 10.7:** *Entire implementation of EchoServer*
``` java
import java.net.*;
import java.io.*;

capsule EchoClient() {

  Socket echoSocket = null;
  PrintWriter out = null;
  BufferedReader in = null;
  BufferedReader stdIn = null;

  void run() {
    try {
      open();
      out.println("Hello␣Server!");
      System.out.println("Server␣replied:␣" + in.readLine());
      out.println("" + System.currentTimeMillis() + ".");
      System.out.println("Server␣replied:␣" + in.readLine());
      out.println("Good␣bye.");
      System.out.println("Server␣replied:␣" + in.readLine());
      close();
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
  }
  private void open() {
    try {
      echoSocket = new Socket("localhost", 8080);
      out = new PrintWriter(echoSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
      stdIn = new BufferedReader(new InputStreamReader(System.in));
    } catch (UnknownHostException e) {
      e.printStackTrace(System.err);
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
  }
capsule EchoClient() {...}
  private void close() {
    try {
      out.close();
      in.close();
      stdIn.close();
      echoSocket.close();
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
  }
}
```

As you can see, capsules can declare helper procedures: open (line 25) and close
(line 38). These are accessible only by the owner capsule.


## Implicit concurrency

This code is very similar to how one would write a sequential program to model
the same scenario, so the structure of this Panini program would be familiar to
a sequential programmer. This code is also free of any concurrency-related
concerns, such as setup and teardown threads, synchronization between the
ConnectionHandlers to access the EchoServer’s ServerSocket.

An example of implicit concurrency is the run procedure of the ConnectionHandler
capsule. All ten capsules will ask the EchoServer capsule for a connection and
once such a connection is obtained any other handler can go acquire another one
without having to wait on any previous handlers to finish communicating with the
clients.

When it is safe to exploit these sources of implicit concurrency, Panini
compiler will automatically introduce parallelism to speedup this program
without intervention from the programmer.
