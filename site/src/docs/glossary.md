---
layout: page
---

* TOC
{:toc}

##### Artifact, (a.k.a. Source Artifact, Generated Artifact)

A Java source code artifact created by @PaniniJ. Key examples include capsule
classes and duck classes.


##### Artifact Generation

The process by which @PaniniJ processes a set of user-defined template classes
and automatically generates/creates derived artifacts.


##### Capsule

An actor-like software construct defined in Panini which uniquely owns its state
variables, provides a set of procedures which can be invoked, and has an
execution profile by which computations of invoked procedures are performed.


##### Capsule, Child

A capsule declared within the definition of another capsule. Note that each
design argument of some capsule C is not counted as a child capsule of C (though
they may well be child capsules of some other capsule).


##### Capsule, Leaf

A capsule having no children. A leaf capsule may be either passive or active.


##### Capsule, Passive

A capsule having no user-defined run() declaration.


##### Capsule, Active

A capsule having a user-defined run() declaration.


##### Capsule, Root

A capsule which is active and has no dependencies. Usually designated with the
@Root annotation.


##### Declaration, design()

Where the user defines the set of design arguments and specifies what capsules
are to be wired to it’s child capsules.


##### Declaration, init()

Where the user defines initialization code for a capsule’s state variables.


##### Declaration, run()

Where the user defines custom run behavior for a capsule. If a capsule has a run
declaration, it is called an active capsule. Otherwise, it is called a passive
capsule.

##### Capsule Requirements

The set of capsules S which must be passed to a capsule C in order for C to be
well-defined.


##### Execution Profile

The mechanism or policy by which a capsule’s procedure invocations are
processed. For example, in the case of the thread execution profile, procedure
invocations are submitted to a queue and processed one-by-one by that capsule’s
own dedicated thread.


##### Future

A thread-safe object/class which represents a result of a task. We say that a
future is resolved when the task is complete and the result is ready to be used.
If a thread tries to use this result before it has been resolved, then the
thread will block until it is resolved.


##### Duck Future

An object/class which is a mockup of one of the user’s objects/classes but also
acts as a future, resolvable by the panini runtime.


##### Method

A regular Java method. (This is distinct from the Panini concept of a procedure.)


##### Method Call

A regular call to a Java method. (This is distinct from the Panini concept of
procedure invocation.)


##### Oracle

When testing whether some computation has computed some result correctly, an
oracle can be queried for the result which that computation should have
computed.


##### Panini

The abstract programming model which defines the semantics of a system of
interacting capsules.


##### PaniniJ

A research language similar to Java which adds support for the capsule-oriented
programming as defined in the Panini programming model.


##### @PaniniJ

An annotation processor for writing Panini programs in Java.


##### Procedure

A panini analog of a method. A procedure is the user-defined code on a capsule’s
interface which can be invoked (i.e. called), potentially by other capsules or
other threads. Arguments can be passed and an object can be returned.
Importantly, the returned object can be a duck future.


##### Procedure Invocation

A panini analog of a method call. (See Procedure.)


##### Shape

A description of a method’s return and argument types. This is essentially the
information in a method signature aside from its names. By extension, we also
say that procedures have shape.


##### Signature

A Panini analog of a Java interface. Each signature specifies a set of
procedures. In order for a capsule to implement a signature, it must have a
definition matching the shape and name of each procedure in that signature.


##### State Variable, also state

A Panini analog of an instance variable on a Java object. A state variable is a
variable attached to a capsule instance. They can only be accessed and modified
by the init() declaration and procedures of the capsule which owns them.


##### System Topology

How a network of capsules is connected.


##### Template Class

A Java class annotated with either @Capsule or @Signature which specifies the
elements of a capsule or signature, respectively. For example, some elements
which a capsule template class is used to define are the procedure definitions,
the define() declaration, and child capsule declarations. It is from processing
a set of template classes that @PaniniJ generates a set of source artifacts.


##### Wiring

The process of initializing a system of capsules with references to one another
according to the user-defined system topology.
