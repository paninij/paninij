---
title: Jargon
short_title: Jargon
permalink: /docs/jargon.html
---

* TOC
{:toc}



##### Panini

A mathematical abstraction of a programming model (a.k.a. a "core calculus") for
modularly analyzing a system of interacting concurrent entities called capsules.
This is used for writing mathematical proofs.


##### @PaniniJ

Java compiler plugin and runtime inspired by Panini to support capsule oriented
programming embedded in Java.


##### Generated Source

A Java source code file created by the @PaniniJ compiler plugin. Key examples:

- The capsule classes automatically generated from user-defined capsule core
  classes.
- Automatically generated duck classes.


##### Capsule

An actor-like software construct defined in Panini which

- Owns its state variables.
- Implements a set of procedures which can be asynchronously invoked by other
  capsules.

A capsule is declared by writing a capsule core class and adding the `@Capsule`
annotation to it. For example, the following will declare a capsule named `Foo`:

``` java
@Capsule
class FooCore {
    // ...
}
```

##### Signature

A Panini analog of a Java interface. Each signature specifies a set of
procedures. Capsules may implement signatures.


##### Capsule Core

A Java class annotated with `@Capsule`. It is used to declare capsule types.
For example, the class `FooCore` below declares an (automatically generated)
capsule type, `Foo`.

``` java
@Capsule
class FooCore {
    // ...
}
```

Within the body of a capsule core class, various syntactic constructs are
interpreted as different parts of a capsule declaration. For example, within a
capsule core class:

- A method named `design` is interpreted as the capsule's design declaration.
- Most methods are interpreted as procedures definitions.
- Fields whose types are capsule types are interpreted as capsule reference
  declarations.

It is by processing a set of capsule core classes that @PaniniJ performs all of
its static checks and source generation.


##### Signature Core

A Java class annotated with `@Signature`. It is analogous to a capsule core
class, but it is used to specify signatures. For example, the class `SigCore`
declares an (automatically generated) signature type, `Sig`.

``` java
@Signature
class SigCore {
    // ...
}
```

A capsule can implement a signature by making the capsule core class implement
the appropriate signature core class. For example,

``` java
@Capsule
class FooCore implements SigCore {
    // ...
}
```


##### Capsule Reference Declaration

A declaration within a capsule that indicates that this capsule relies on some
other capsule. A capsule reference is declared by adding to a capsule core class
a field of capsule type. For example, the following capsule `Foo` declares a
capsule reference to some other capsule of type `Bar` which in this context is
named `b`.

``` java
@Capsule
class FooCore {
    @Imported Bar b;
    // ...
}
```

This means that the correct behavior of `Foo` depends upon the availability of a
`Bar` capsule instance.

The fields being used to declare a capsule reference are initialized by the
runtime. Client code should never modify these fields.

There are two different kinds of capsule reference declarations: `@Local` and
`@Imported`.


##### Local Capsule Reference Declaration

A capsule reference declaration annotated with `@Local`. For example,

``` java
@Capsule
class FooCore {
    @Local Bar bar;
    // ...
}
```

A `@Local` capsule reference indicates that for every instance of the containing
capsule, the runtime should instantiate an instance of the contained capsule.

In the above example, this means that for every `Foo` instance the runtime will
make a distinct `Bar` instance, and in the context of that `Foo`, the `Bar` can
be accessed by the name `bar`.

This is one of two kinds of capsule references. The other kind is `@Imported`.


##### Imported Capsule Reference Declaration

A capsule reference declaration annotated with `@Imported`. For example

``` java
@Capsule
class FooCore {
    @Imported Bar bar;
    // ...
}

@Capsule
class BarCore {
    // ...
}

@Root @Capsule
class MainCore {
    @Local Foo foo;
    @Local Bar bar;
    void design(Main self) {
        foo.imports(bar);
    }
}
```

An `@Imported` capsule reference indicates that every instance of the containing
capsule, will need to be provided with a capsule instance of the contained type.
It is the responsibility of some other capsule's design declaration to indicate
how an `@Imported` capsule should be provided. This so-called "wiring" is done
using the `imports()`.

In the above example, for every instance of a `Main` capsule, there is one
`Foo` and one `@Bar`. (See `@Local` capsule reference declaration.) The `Main`
capsule, because it declared a `@Local Foo`, is responsible for indicating where
the `Bar` that it requires should come from. This is specified inside of the
`Main` capsule's design declaration by calling the `imports()` method as
appropriate.

This is one of two kinds of capsule references. The other kind is `@Local`.


##### Wiring

The process of initializing a system of capsules with references to one another
according to the user-defined system topology.


##### Root Capsule

A capsule declaration with an additional `@Root` annotation. This is often an
active capsule which has various `@Local` capsule declarations wired together
as appropriate. All of a `@Root` capsule's capsule reference declarations must
be `@Local`.


##### Passive Capsule

A capsule without a run declaration. A passive capsule *reacts* to other
capsules invoking its procedures. It may also invoke procedures on other
capsules.


##### Active Capsule

A capsule with a run declaration. An active capsule *drives* other capsules by
invoking their procedures. Such a capsule has no procedures. Once the capsule
system is appropriately instantiated, the runtime will execute the body of an
active capsule's run declaration. Once this has completed, the capsule will
terminate.


##### Design Declaration

A method named `design` on a capsule core class. This is where the user defines
wirings between capsules. For example,

``` java
@Capsule
@Root @Capsule
class MainCore {
    @Local Foo foo;
    @Local Bar bar;
    void design(Main self) {
        foo.imports(bar);
    }
}
```

See imported capsule references for more information.


##### Init Declaration

A method named `init` on a capsule core class. This is where the user can
define initialization code for a capsule’s state variables. This is similar to
a Java constructor. This method will be called during capsule instantiated. This
will be called before any of a passive capsule's procedures will be invoked and
before an active capsule's run declaration will be executed.


##### Run Declaration

Where the user defines the complete sequential behavior of a capsule. If a
capsule has a run declaration, it is called an active capsule. Otherwise, it is
called a passive capsule.


##### Capsule Imports

The set of capsules which must be provided to a capsule instance of type `C` in
order that instance of `C` to be well-defined. (See imported capsule reference
declarations.)


##### State

A Panini analog of a Java instance variable (i.e. field). A state is a variable
attached to and contained within a capsule instance.  Furthermore, the objects
reachable from a capsule's state are owned by that capsule. These owned
variables and objects should only be accessed and modified by the capsule which
owns it.

In @PaniniJ, states are simply the fields of a capsule core class which are not
interpreted as capsule reference declarations (i.e. those fields not marked
with either `@Local` or `@Imported`).


##### Capsule Execution Profile

The policy by which a capsule’s procedure invocations are processed. For
example, in the case of the thread execution profile, procedure invocations are
asynchronously submitted to a queue and processed one-by-one by the invoked
capsule's own dedicated thread.


##### Procedure

A Panini analog of a method. A procedure is the code attached to a capsule which
can be invoked (potentially) asynchronously by other capsules. Arguments can be
passed and a result can be returned.

For example, below the `Foo` capsule declares three procedures: `getPair()`,
`getString()`, and `usePair()`.

``` java
public class Pair {
    int fst;
    int snd;
}

@Capsule
class FooCore {
    Pair getPair() {
        Pair p;
        // Do some work and initialize `p`...
        return p;
    }

    @Future String getString() {
        String s;
        // Do some work and initialize `s`...
        return s;
    }

    void usePair(Pair p) {
        // Use `p` somehow.
    }
}
```

Both procedures are invoked asynchronously. `getPair()` will return a duck
future whereas `getString()` will return an explicit future. Invocations of
`usePair()` must provide a `Pair` argument.


##### Procedure Invocation

A Panini analog of a method call. (See Procedure.) In @PaniniJ, the syntax looks
just like Java method invocation syntax.


##### Future

A thread-safe object/class which represents a result of a task. We say that a
future is resolved when the task is complete and the result is ready to be used.
If a thread tries to use this result before it has been resolved, then the
thread will block until it is resolved. In `@PaniniJ`, there are two kinds of
futures: duck futures and explicit futures.


##### Duck Future

An object which can act as a future but is also a subtype of some "normal" type.
These are generated by the @PaniniJ runtime to remove some of the boilerplate
from using explicit futures. A duck future "looks" like and "acts" like a normal
object, but it will block if a method is called on it before the future has been
resolved.


##### Explicit Future

Explicit futures may be returned from procedure invocations annotated with
`@Future`. Such procedures will return a `java.util.concurrent.Future` instance
boxing the result of the procedure invocation. Unlike a duck future, an explicit
future must be manually unboxed by calling `Future#get()`.
