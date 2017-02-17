---
part: 1
chapter: 1
title: Motivation
lede: >
  The Panini programming language is designed to enable implicit concurrency as
  a direct result of modularization of a system into capsules and to maintain
  modular reasoning in the presence of concurrency.
---

## A Pressing Need

There is no escape: all programmers will soon be forced to consider concurrency
decisions in software design. Most modern software systems tend to be
distributed, event-driven, and asynchronous, often requiring components to
maintain multiple threads for message and event handling. There is also
increasing pressure on developers to introduce concurrency into applications in
order to take advantage of multicore processors to improve performance.

Yet concurrent programming stubbornly remains difficult and error-prone. First,
a programmer must partition the overall system workload into tasks. Second,
tasks must be associated with threads of execution in a manner that improves
utilization while minimizing overhead; note that this set of decisions is highly
dependent on characteristics of the platform, such as the number of available
cores. Finally, the programmer must manage the dependencies, interactions, and
potential interleavings between tasks to maintain the intended semantics and
avoid concurrency hazards, often by using low-level primitives for
synchronization. To address these issues, the invention and refinement of better
abstractions is needed. We need abstractions which can hide most details of
concurrency from the programmer and instead allow them to focus on program
logic.

The significance of better abstractions for concurrency is not lost on the
research community. Tremendous work has been done in this area. However, we
believe that a major gap remains. There is an impedance mismatch between
sequential and implicitly concurrent code written using existing abstractions
that is hard for a sequentially trained programmer to overcome. These
programmers typically rely upon the sequence of operations to reason about their
programs.


## Running Example

To illustrate the challenges of concurrent program design, consider a simplified
version of the classic arcade game, Asteroids. The game consists of five
components: ship (`Ship`), game logic (`Logic`), user input (`Input`),
controller (`Asteroids`), and a `UI`.

The user input listens to the keyboard, parses the input, and directs the ship
to make corresponding moves. The logic component maintains the board
configuration, generates new asteroids, computes whether any previously
generated asteroids have been destroyed by the ship, and computes whether the
ship has collided with an asteroid. The ship moves and fires rockets as
directed. The controller mediates the model and the view.


## Difficult Concurrency-related Design Decisions

The modular structure of the system is clear from the description above, and it
is not difficult to define five Java classes with appropriate methods
corresponding to this design. However, the system will not yet work. The
programmer is faced with a number of nontrivial decisions: Which of these
components needs to be associated with an execution thread of its own? Which
operations must be executed asynchronously? Where is synchronization going to be
needed? A human expert might reach the following conclusions, shown in listing
2.1.

- A thread is needed to read the user input (line 52)

- The UI, as usual, has its own event-handling thread. The calls on the UI need
  to pass their data to the event handling thread via the UI event queue (lines
  26 and 28).

- The game logic needs to run in a separate thread; otherwise, calls to update
  game state will "steal" the controller thread and cause the game to become
  jittery.

- The Ship class does not need a dedicated thread, however, its methods need to
  be synchronized, since its data is accessed by both the user input thread and
  the controller thread.

**Listing 2.1:** Program for a simplified arcade game Asteroids.

``` java
class Ship {
    private short state = 0;
    synchronized void die() { state = 2; }
    synchronized  void fire() { state = 1; }
    synchronized boolean alive() { return state != 2; }
    synchronized boolean isFiring() {
        if(state == 1) { state = 0; return true; }
            return false;
        }
    private int x = 5;
    synchronized int getPos() { return x; }
    synchronized  void moveLeft() { if (x>0) x--; }
    synchronized  void moveRight() { if (x<10) x++; }
}

public class Asteroids extends Thread {
    Ship s; UI ui; Logic l;
    public void run() {
        int points = 0;
        while(s.alive()) {
            int shipPos = s.getPos();
            boolean isFiring = s.isFiring();
            int result = l.step(shipPos, isFiring);
            if(result>0) points += result;
            else if (result<0) s.die();
            ui.repaint(shipPos, isFiring, points);
        }
        ui.endgame();
    }

    public static void main(String[] args) {
        Ship s = new Ship();
        Input i = new Input(s);
        Logic l = new Logic();
        UI ui = new UI(l);
        Asteroids c = new Asteroids(s, ui, l);
        c.start();
        i.start();
        try {
            i.join();
            c.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Asteroids(Ship s, UI ui, Logic l) {
        this.s = s; this.ui = ui; this.l = l;
    }
}

class UI { /* provides repaint, endgame */ }
class Input extends Thread { /* reads player input */ }
class Logic { /* provides step */ }
```
{: .code-with-line-numbers}

None of the conclusions above, in itself, is difficult to implement in Java.
Rather, in practice it is the process of visualizing the interactions between
the components, in order to reach those conclusions, that is extremely
challenging for programmers [2] [1].

[1] ACM/IEEE-CS Joint Task Force. Computer science curricula 2013 (CS2013).
Technical report, ACM/IEEE, 2012.

[2] D. Meder, V. Pankratius, and W. F. Tichy. Parallelism in curricula an
international survey. Technical report, University of Karlsruhe, 2008.
