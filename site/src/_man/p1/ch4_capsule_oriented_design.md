---
part: 1
chapter: 4
title: Capsule-Oriented Design
quote: Simplicity is the ultimate sophistication.
quote_source: Leonardo da Vinci
---

## Asteroids in Panini

We will illustrate capsule-oriented program design and Panini’s new features
using a simplified version of the classic arcade game Asteroids. In this game
objective of the player is to score as many points as possible by destroying
asteroids. In the simplified game, the player controls a ship that can move left
or right. The ship can also fire.

For our problem, the subproblems are modeling the ship, game logic, user input,
controller, and UI. The user input component would listen to the keyboard, parse
the input, and direct the ship to make corresponding moves. The logic component
would maintain the board configuration, generate new asteroids, compute whether
any previously generated asteroids have been destroyed by the ship, and compute
whether the ship has collided with an asteroid. The ship would move and fire
rockets as directed by player. Finally, the controller would mediate the model
and the view.

The modular structure of the system is clear from the description above, and it
is not difficult to define five modules with appropriate methods corresponding
to this design. However, the system will not yet work. The programmer is faced
with a number of nontrivial decisions related to concurrency: Which of these
components needs to be associated with an execution thread of its own? Which
operations must be executed asynchronously? Where is synchronization going to be
needed? A human expert might reach the following conclusions.

1. An independent thread of control is needed to read the user input

2. The UI, as usual, has its own event-handling thread of control. The calls on
the UI need to pass their data to the event handling thread via the UI event
queue

3. The game logic needs to run in a separate thread of control; otherwise, calls
to update game state will “steal” the controller thread of control and cause the
game to become jittery

4. The Ship class does not need a dedicated thread of control, however, its
methods need to be synchronized, since its data is accessed by both the user
input thread and the controller thread

None of the conclusions above, in itself, is difficult to implement in existing
programming languages. Rather, in practice it is the process of visualizing the
interactions between the components, in order to reach those conclusions, that
is extremely challenging for programmers.

Capsule-oriented programming paradigm and the Panini language removes most of
this burden so that the programmer can focus on their application’s logic.


## Architecture and Design

In capsule-oriented programming better design leads to better implicit
concurrency, so it is valuable to start off with the architecture and design.

The Panini programmer specifies a system as a collection of capsules,
signatures, and ordinary object-oriented classes. A capsule is an abstraction
for decomposing software into its parts, and a design is a mechanism for
composing these parts together. So the first order of business is to come up
with this capsule-oriented design. This involves creating capsules, assigning
subtasks to these capsules, designing how these capsules could be connected to
each other, and finally integrating them to form a complete program. Each of
these steps is fairly straightforward and is done entirely in the program.

### Create capsules and assign responsibilities to capsules

In assigning responsibility, follow the information-hiding principle. There are
four design decisions that are likely to change: ship’s representation, game
logic, UI design, and how we get input from the player. Therefore, we must hide
these responsibility behind interfaces of separate capsules. Finally, capsule
Asteroids would encapsulate the design decisions related to controller logic.

**Listing 4.1:** Design skeleton for Asteroids
``` java
capsule Ship() { }
capsule Game() { }
capsule UI() { }
capsule Input() { }
capsule Asteroids () { }
```

This decision, to implement these components using the capsule abstraction,
automatically handles four concurrency concerns mentioned above. Each capsule
behaves as if it has an independent thread of control, which resolves issues 1-3
above. Only a single thread of control can ever access a capsule’s data, which
resolves the fourth issue.

This illustrates the value proposition of the programming paradigm and the
programming language: to enable greater program modularity and in doing so
automatically enable greater program concurrency. Observe that Panini does not
use explicit concurrency features. Instead, the programmer modularizes a program
using capsules, which implicitly specify boundaries outside of which concurrency
can occur. The Panini runtime will automatically enable concurrency in between
the boundaries of capsules when safe to do so.

Programmers familiar with the notion of actors may notice some similarities.
However, capsules differs from extant work on actors in three significant ways
that we believe is helpful for programmers. First, all inter-capsule
communication is logically synchronous. Second, capsules, by default, provide
confinement without requiring additional annotations from programmers. Third,
topology of a capsule-oriented program is fixed at compile-time, which aids in
static analysis of properties such as sequential consistency and confinement and
with some implementation algorithms, e.g. garbage collection. Here, by topology
we mean capsule instances and their interconnections.

Capsule-oriented programming eliminates two classes of concurrency errors by
construction: sequential inconsistency and race conditions due to shared data.


### Design interconnection between capsules.

We do not yet know the interconnection between these five capsules, but it seems
to be the case that Input ought to direct the ship to take actions, and UI might
need information from Game to present it. Finally, controller Ateroids would
need to talk to all of the capsules to be able to control their actions. We can
use this knowledge to refine our architecture and design.

**Listing 4.2:** *Interconnection between Capsules in Design for Asteroids*

``` java
capsule Ship() { }
capsule Game() { }
capsule UI(Game g) { }
capsule Input(Ship s) { }
capsule Asteroids () { }
```

The third line says that the UI capsule could be connected with an external Game
capsule. Alternatively, we could read the third line as: “the UI capsule
requires a Game capsule.” The fourth line says that the Input capsule could be
connected with an external Ship capsule, and the first, the second, and the
fifth lines says that the Ship, the Game, and the Asteroids capsules may not be
connected to any other external capsules.

Integrate capsules to form a complete program. We now know that this program
would require one Ship, one UI, one Game, and an Input. These will be
co-ordinated by a controller. At this time, we can choose between two
alternative designs: have the controller capsule Astroid contain other capsules,
or create a new Capsule for that responsibility. We choose to assign this
responsibility to the Astroid example, but the other choice is certainly
feasible.

The listing below shows this design of the Asteroids program.

**Listing 4.3:** *Complete Capsule-oriented Design for Asteroids*

``` java
capsule Ship() { }
capsule Game() { }
capsule UI(Game g) { }
capsule Input(Ship s) { }
capsule Asteroids () {
  design {
    Ship s; UI ui; Game g; Input i;
    ui(g); i(s);
  }
}
```

Lines six to nine are the new parts of the design for this system. They define
the internal design for the Asteroids capsule. This declarative design says that
this capsule would have four internal components, one of each kind defined
previously. In other word, one capsule instance s of kind Ship, another instance
ui of kind UI, an instance g of kind Game, and another instance i of kind Input.

Unlike object instances, capsules instances do not need to be created using a
new expression. It is sufficient to just declare them like line seven above.

Line eight defines interconnections between capsule instances. Line eight says
that the capsule instance ui would be connected with the capsule instance g and
capsule instance i would be connected with the capsule instance s.

### Check the design.

A nice property of Panini is that once you have written the high-level design
above, you can check it using the Panini compiler to find out whether you got
the capsule definitions and their interconnections right. Copy and paste the
code above in a file Asteroids.java and compile it using the Panini compiler.

To compile this program simply run:

```
$ panc Asteroids.java
```

This program is not yet executable, but with a small change we can make it
executable by adding a run procedure in the Asteroids capsule.

**Listing 4.4:** *Executable Capsule-oriented Design for Asteroids*

``` java
capsule Ship() { }
capsule Game() { }
capsule UI(Game g) { }
capsule Input(Ship s) { }
capsule Asteroids () {
  design {
    Ship s; UI ui; Game g; Input i;
    ui(g); i(s);
  }
  void run() {
    System.out.println("TODO: fill in implementation");
  }
}
```

To compile this program simply run:

```
$ panc Asteroids.java
```

You can then run this panini program with:

```
$ panini Asteroids
```


## Implementation

### Capsule Ship.

We can now start specifying behavior of each of these capsules. The behavior of
capsule ship is fairly straightforward, it should provide facilities to move
left and right, to fire, to kill itself, and to check its position and state.

The behavior of the capsule Ship requires keeping track of its position and its
condition. In Panini, a capsule can declare states to keep track of such pieces
of information. A state declaration is syntactically the same as a field
declaration in object-oriented languages, however, it differs semantically in
two ways: first, a state is private to a capsule (there are no public,
protected, or static modifiers.), second, all the memory locations that can be
reached via this state are uniquely owned by the containing capsule instance.
Other capsule instances may not directly access it.

**Listing 4.5:** *Capsule Ship and its States*

``` java
capsule Ship {
  short state = 0;
  int x = 5;
}
```

The listing above shows two states on lines 2 and 3. You could also write state
initializers to give them initial values, or you could write a capsule
initializer as shown in the listing below.

**Listing 4.6:** *Capsule Ship with an Initializer*
``` java
capsule Ship {
  short state;
  int x;
  => {
    state = 0;
    x = 5;
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
concurrency in Panini programs. Several example procedures of the capsule Ship
are shown below.

**Listing 4.7:** *Complete Implementation of the Capsule Ship*
``` java
capsule Ship {
  short state = 0;
  void die() { state = 2; }
  void fire() { state = 1; }
  boolean alive() { return state != 2; }
  boolean isFiring() {
    if(state == 1) { state = 0; return true; }
    return false;
  }

  int x = 5;
  int getPos() { return x; }
  void moveLeft() { if (x>0) x--; }
  void moveRight() { if (x<10) x++; }
}
```

*Concurrency concerns in Ship’s Design.* Recall from our previous discussion
that a ship’s data is accessed by both the user input component and the
controller component. Therefore, in an object-oriented design a human expert may
conclude that all of its procedures need to be synchronized. A capsule’s
semantics gives this behavior by default: it ensures that the ship’s data is
accessed only by a single thread of control, ever. Thus, this concurrency
concern is automatically addressed.


### Capsule Asteroids.

The behavior of the capsule Asteroids is specially interesting. This capsule
declares an autonomous capsule procedure run on line six. Capsule Asteroids is a
closed capsule

A capsule is considered closed, if it does not require access to external
capsule instances. In our example, Asteroids is a closed capsule, whereas Input
is not. A closed capsule is a complete Panini program, and if it defines
autonomous behavior, it can be executed.

**Listing 4.8:** *Capsule Asteroids*

``` java
capsule Asteroids {
  design {
    Ship s; UI ui; Game g; Input i;
    ui(l); i(s);
  }
  void run() {
    int points = 0;
    while(s.alive()) {
      int shipPos = s.getPos();
        boolean isFiring = s.isFiring();
        int result = g.step(shipPos, isFiring);
        if(result>0) points += result;
        else if (result<0) s.die();
        ui.repaint(shipPos, isFiring, points);
        yield(1000);
    }
    ui.endGame();
  }
}
```

The execution of this program begins by allocating memory for all capsule
instances, and connecting them together as specified in the design declaration
on lines 2-5. Recall that capsule parameters define the other capsule instances
required for a capsule to function. A capsule listed in another capsule’s
parameter list can be sent messages from that capsule. Design declarations allow
a programmer to define the connections between individual capsule instances.
These connections are established before execution of any capsule instance
begins.

Next, any capsule with a run procedure begins executing independently as soon as
the initialization and interconnection of all capsules is complete and may
generate calls to the procedures of other capsules. For example, referring to
the code above, capsule Asteroids will run code on 6-18. Capsules without a run
procedure, such as Ship, perform computation only when their procedures are
invoked. For example, on lines 8,9,10, and 13 procedures of the capsule Ship are
invoked on the capsule instance s.

### Capsule Input.

A simple implementation of the capsule Input is shown below.

**Listing 4.9:** *Capsule Input in Asteroids*

``` java
capsule Input (Ship ship) {
  void run() {
    try {
      while(ship.alive()) {
        switch(System.in.read()) {
          case 106: ship.moveLeft(); break;  //Key j
          case 108: ship.moveRight(); break; //Key l
          case 105: ship.fire(); break;      //Key k
        }
      }
    } catch (IOException ioe) { /* ... */ }
  }
}
```

This implementation continually checks for user input and directs the ship to
move left, right or fire based on the key pressed.

*Concurrency concerns in Input’s Design.* Note that the semantics of a capsule,
i.e. each capsule instance runs as if it has a logically, independent thread of
control, naturally satisfies the requirements of the Input capsule.

### Capsules Game and UI.

These capsules implement the game logic and a user interface that shows position
of the ship, and the asteroids. A full implementation is available in the Panini
distribution.

## Analysis of Benefits

This example illustrates some of the key advantages of the capsule-oriented
approach for programmers. These are:

- They don’t need to create explicit threads or specify whether a given capsule
needs its own thread of execution.

- They don’t need to recognize or reason about potential data races due to shared
data.

- They work within a familiar method-call style interface with a reasonable
expectation of sequential consistency.

- All synchronization-related details are abstracted away and are fully
transparent to them.


## Compiling and running Asteroids!

If you haven’t installed the Panini compiler yet then please see chapter 13 on
installing and running the compiler.

You should have received a copy of the full Asteroids program as part of the
Panini distribution. This program is located in the directory examples within
the Panini distribution. Copy and save it, say, to the file Asteroids.java in
your local directory. If you have put the Panini compiler and the Panini
executable in your path, you can compile the program by simply running:

To compile this program simply run:

```
$ panc Asteroids.java
```

You can then run this panini program with:

```
$ panini Asteroids
```

Where Asteroids is the name of the closed capsule that contains other capsule
instances.

Now that you’ve done your first capsule-oriented design it is time to
systematically familiarize yourself with different features of the Panini
programming language.
