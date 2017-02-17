---
part: 2
chapter: 5
title: Capsule Declarations
quote: Simplify, simplify.
quote_source: Henry D. Thoreau
---

The Panini programmer specifies a design as a collection of capsules and
ordinary object-oriented classes.

A *capsule* is an abstraction for decomposing a software into its parts. A
capsule is like an information-hiding module in that it defines a set of public
operations, hides the implementation details, and could serve as a work
assignment for a developer or a team of developers. Beyond these standard
responsibilities, a capsule also serves as a memory region, for some set of
standard object instances and behaves as an independent logical process.

The notion of a capsule in the Panini programming language is designed to enable
implicit concurrency at the interface of capsules as a direct result of the
modularization of a design into capsules, and to maintain modular reasoning in
the presence of implicit concurrency. Here, by modular reasoning we mean our
ability to understand a software one module at a time by looking at the code for
that module and only the interfaces of other modules referenced by name.

Inter-capsule calls look like ordinary method calls to the programmer. The
object-oriented features are standard, but there are no explicit threads or
synchronization locks in Panini.

A program in Panini can have zero or more signature declarations, zero or more
class declarations, and zero or more capsule declarations. Panini classes are
very similar to classes in other object oriented languages but with the
restriction that they cannot contain any explicit concurrency constructs.

## Syntax of Capsule Declaration

Capsules are designed to have the feel of ordinary classes, the intent being to
capitalize on programmers’ familiarity with object-oriented design and thus
minimize the learning curve. Each capsule guarantees that its state is accessed
by only one thread, thereby maintaining thread safety with respect to state
mutations (by confinement in this case). Implicitly concurrent procedure calls
on capsules are structured to have the appearance of ordinary method calls.

Capsules define a set of public operations as well as a memory region. They can
have parameters, state declarations, inner class declarations, initializer, and
procedure definitions.

The syntax of a capsule declaration is as follows.

**Listing 5.1:** *Syntax of Capsule Declarations*

```
capsule CapsuleName [ (Param p1, ..., Param pn) ] [ implements SignatureName+ ] {
  [Initializer]
  [DesignDeclaration]
  StateDeclaration*
  ClassDeclaration*
  procedure*
}
```

Here, `[s]` means `s` is optional, `s*` means zero or more of `s`, and `s+`
means one or more of `s`.

A capsule declaration consists of the keyword ‘capsule’, the name of the
capsule, zero or more formal parameters representing dependencies on other
capsules, and zero or more signatures representing services that the capsule
provides, followed by the capsule’s implementation. Each procedure declaration
in every signature implemented by the capsule must match with exactly one
capsule procedure. Panini does not have capsule inheritance but does have class
inheritance. The primary mechanism for reuse of capsules is composition.

Let us start with an empty capsule declaration shown in listing 5.2. This
declaration just gives the capsule a name C.

**Listing 5.2:** *An empty capsule declaration*
``` java
capsule C {
    // Nothing in here.
}
```
{: .code-with-line-numbers}

Capsules can require access to instances of other capsules. For example, in
listing 5.3 is a new version of the capsule C that requires access to an
instance of another capsule D. Notice that when C did not have any requirements,
e.g. in listing 5.2 we can omit ‘(’ and ‘)’ in capsule declaration.

**Listing 5.3:** An example capsule ‘C’ that requires a capsule instance of kind ‘D’

``` java
capsule D () {
    ...
}
capsule C (D d) {
    ...
}
```
{: .code-with-line-numbers}

Capsules can also require access to primitive, reference, or array values. For
example, in listing 5.4 is another version of capsule C that requires access to
an instance of another capsule D, a boolean value, a string, and an array of
strings.

**Listing 5.4:** *An example capsule ‘C’ that also requires other values*

``` java
capsule D () {
    ...
}
capsule C (D d, boolean b, String s, String[] args) {
    ...
}
```
{: .code-with-line-numbers}

Capsules can implement signatures. An example is shown in listing 12.1. In this
listing an empty signature S is implemented by the capsule D.

**Listing 5.5:** *Signatures and capsules*

``` java
signature S {
}
capsule D () implements S {
    ...
}
capsule C (S d, boolean b, String s, String[] args) {
    ...
}
```
{: .code-with-line-numbers}

Capsules can require access to another capsule instance that implement certain
signatures. For example, in listing 12.1 capsule C now requires access to an
instance of a capsule that implements signature S. Since the capsule D in this
listing declares to implement this signature an instance of D could be provided
to C, but it is also possible to provide instances of other capsules that also
implement the signature S. More on signatures in chapter 7.

### Closed capsules

A capsule is considered closed, if it does not require access to external
capsule instances. In listing 12.1, D is a closed capsule, whereas C is not a
closed capsule because it requires access to another capsule instance of type S.
A closed capsule is a complete Panini program. If a closed capsule provides
autonomous behavior it can be executed.


## Capsule States

A capsule can declare states to keep track of its internal information. A state
declaration has a type, a name, and optionally an initialization expression.
Legal types for a state declaration are primitive types and reference types.

For example, in listing 5.6, the capsule D declares a state of reference type
String and the capsule C declares a state i of primitive type int and another
state privList of reference type ArrayList<Integer>.

**Listing 5.6:** *State declarations in capsules*

``` java
import java.util.ArrayList;
signature S {
}
capsule D () implements S {
    String name = "D";
    ...
}
capsule C (S d, boolean b, String s, String[] args) {
    int i;
    ArrayList<Integer> privList = new ArrayList<Integer>();
    ...
}
```
{: .code-with-line-numbers}

A state looks similar to a field in traditional class declarations, but there
are two major differences.

State is always private. All state declarations are private to a capsule,
therefore, no visibility modifiers are necessary.

States are truly encapsulated. A capsule instance controls all accesses to the
object graph reachable from its states. More precisely, a capsule instance acts
as a dominator for the object graph reachable from its states. More on that in
chapter 8.

The listing 5.6 also illustrates using the type ArrayList available in the Java
development kit (JDK). A type declared elsewhere can be imported using the
import statements that has the same syntax as Java.

![Caution!](/img/man/caution.png) A capsule name is not a legal type for a state
declaration.


## Capsule Initializer

To setup the internal data structures of the capsule before it can interact with
external entities, capsules can optionally declare a single initializer.

A capsule initializer runs before any other external procedure calls on a
capsule instance.

As a concrete example, let us add a simple initializer to the capsule C that
will initialize the values of i and privList.

**Listing 5.7:** *Initializer in capsules*
``` java
import java.util.ArrayList;
signature S {
}
capsule D () implements S {
    String name = "D";
    ...
}
capsule C (S d, boolean b, String s, String[] args) {
    int i;
    ArrayList<Integer> privList;

    => {   //Start of an initializer
        i = 42;
        privList = new ArrayList<Integer>();
    }
    ...
}
```
{: .code-with-line-numbers}

A capsule initializer runs without interruption until completion. A desirable
initializer should terminate, although there are no restrictions in Panini on
initializer’s structure to ensure termination.

![Caution!](/img/man/caution.png) A long running capsule initializer can render
a capsule unresponsive.


## Capsule Procedures

After the capsule initializer has completed setting up the data structures of
the capsule instance, capsule instance can receive external procedure calls.

A capsule procedure has a return type, a name, zero or more arguments, and a
body. For example, in listing 5.8 on lines 7-10 there is a procedure append that
takes a single argument s of type String, adds s to name and returns s.

**Listing 5.8:** *Capsule procedures*
``` java
import java.util.ArrayList;

signature S {
    String append (String s);
}

capsule D () implements S {
    String name = "D";
    String append (String s) {
        name += s;
        return s;
    }
}

capsule C (S d, boolean b, String s, String[] args) {
    int i;
    ArrayList<Integer> privList;

    => {   //Start of an initializer
        i = 42;
        privList = new ArrayList<Integer>();
    }

    private void add (Integer n) {
        privList.add(n);
    }

    ...
}
```
{: .code-with-line-numbers}

A capsule procedure looks similar to a method in traditional class declarations,
but there are three major differences.

- *Procedures run logically synchronously.* Unlike methods that run
  synchronously, i.e. when a method is called caller is blocked until caller
  finishes and returns, capsule procedures run logically synchronously, i.e.
  when a procedure is called caller is blocked only if it needs the result right
  away, otherwise caller and callee can compute concurrently.

- *Procedures have exclusive access to states.* A capsule procedure has
  exclusive access to the states of the capsule while it is running. Therefore,
  a programmer need not use locks or other synchronization mechanisms to gain
  exclusive access to states.

- *Procedures are by default public.* All procedure declarations are by default
  public for a capsule, therefore, no visibility modifiers are necessary to make
  them public.

Helper procedures can be declared by qualifying them with a modifier private.
For example, in listing 5.8 on lines 21-23 there is a helper procedure add.

All capsule procedures, except helper procedures, constitute the interface of
the capsule.


## Autonomous Capsule Behavior

Capsule procedures like append and add are passive in that they cause the
capsule to compute in response to a procedure call. A capsule can also declare
autonomous behavior that does not require external call to run. There is one
designated optional capsule procedure run representing that the capsule can
start computation without external stimuli.

**Listing 5.9:** *Autonomous capsule behavior*

``` java
import java.util.ArrayList;
signature S {
}
capsule D () implements S {
    String name = "D";
    String append (String s) {
        name += s;
        return s;
    }
}
capsule C (S d, boolean b, String s, String[] args) {
    int i;
    ArrayList<Integer> privList;

    => {   //Start of an initializer
        i = 42;
        privList = new ArrayList<Integer>();
    }

    private void add (Integer n) {
        privList.add(n);
    }

    void run() {
        for(Integer i = 0; i < 25; i++) {
            add(i);
            d.append("_" + i);
        }
    }
}
```
{: .code-with-line-numbers}

For example, in listing 5.9 on lines 24-29 there is an autonomous procedure run
that runs as soon as memory allocation and initialization for capsules is
complete.

![Caution!](/img/man/caution.png) A capsule can either be autonomous by
declaring a run procedure or passive and declare other procedures, but not both.


## Capsule Procedure Calls

The semantics of capsule procedure calls is new and worth emphasis. In listing
5.9 the body of the autonomous procedure run shows two examples of capsule
procedure calls on lines 26 and 27.

Calling an internal helper procedure.  On line 26 is an example of calling
helper procedure add, which runs to completion while the caller is blocked. This
is to ensure that only a single capsule procedure has exclusive access to the
states of the capsule.

Calling an external capsule procedure.  On line 27 is an example of calling
external capsule procedure append. This call immediately returns, while the
callee continues to finish running the append procedure. This is because the
caller does not make use of the return value of the append procedure.

**Listing 5.10:** *Inter-capsule procedure call*

``` java
void run() {
    for(Integer i = 0; i < 25; i++) {
        add(i);
        String result = d.append("_" + i);
    }
}
```
{: .code-with-line-numbers}

In listing 5.9 on line 4 is another example of calling external capsule
procedure append. This call waits until the caller has finished running the
append procedure. This is because here the caller needs the return value of the
append procedure. So we must wait until that value is available.

A distinct advantage of capsule-oriented programming is that the programmer does
not need to explicitly program these semantic variations.


## Shutdown and Exit Procedures

Each capsule implicitly supports two built-in procedures: shutdown and exit.

When a capsule is requested to shutdown, and if it does not have any pending
work, it terminates.

When a capsule is requested to shutdown, and if it has pending work, it puts the
shutdown request at the end of its work queue and continues to serve other
requests.

When a capsule is requested to exit, it terminates as soon as all of the pending
work (as of the exit request) is done.


## Example: Bank Account

To review features of a capsule declaration, we would now use them to build a simplified banking application. As a concrete example of capsules consider this simple model of a bank account in listing 5.11.

**Listing 5.11:** *A Bank Account Capsule*

``` java
capsule BankAccount() {
   //this declaration represents the state of the capsule
  double balance = 0.0;

  /* Capsule procedures are defined in the same way object
     methods are, and as far as the programmer is concerned
     they behave largely like a normal class method */
  void deposit(double money) {
    balance += money;
  }

  void withdraw(double money) {
    if(balance - money &lt; 0) {
      throw new InvalidTransactionException();
    }
    balance -= money;
  }
}
```
{: .code-with-line-numbers}


## Implicit concurrency

Now consider an example where you have two clients who have a joint bank account. At some point, both of them might try to withdraw money at the same time. In traditional programming languages, if no explicit synchronization is used, this would be a data-race.
Here comes Panini’s greatest strength, the programmer does not have to worry about any of that! He/she can write the out the logical design of the design and the concurrency related issues are dealt with behind the scenes.
Below is an example of such a design where the potential for error is eliminated by the language itself:

**Listing 5.12:** *Multiple Clients of the Bank Account Capsule*

``` java
capsule Bank {
 design {
  BankClient client1;
  BankClient client2;
  BankAccount jointAccount;

  /* Since client1 and client2 are both instantiations
     of a capsule with a run procedure, they will be
     executed concurrently. They will safely access
     the bank account with no need to modify the original
     implementation of either the BankClient or the
     BankAccount capsules. */
  client1(jointAccount);
  client2(jointAccount);
 }
}
```
{: .code-with-line-numbers}
