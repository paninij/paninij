---
part: 2
chapter: 8
title: Capsule State Confinement
---

As mentioned previously all capsule states are private to a capsule. This notion
of privacy is a bit stricter compared to object-oriented languages to promote
stronger encapsulation. A capsule instance confines access to its state.
Stronger encapsulation aids with safe concurrency in capsule-oriented programs.

## Confinement between Instances of a Capsule

For example, consider the listing below.

**Listing 8.1:** *Confinement violation between capsule instances*

``` java
import java.util.ArrayList;
capsule C(C other) {
 ArrayList<Integer> privList = new ArrayList<Integer>();
 void test() {
  other.privList.add(42);
 }
}

capsule TConfineInstance {
 design {
  C c1 ; C c2;
  c1(c2); c2(c1);
 }
 void run() {
  c1.test();
 }
}
```
{: .code-with-line-numbers}

When compiled the Panini compiler will produce a compile-time error:

```
$ panc TConfineInstance.java
 TConfineInstance.java:8: error: States of capsules cannot be accessed directly.
 other.privList.add(42);
 ^       1 error
```

This is because in the capsule `C`, internal encapsulated state of the other
capsule is directly accessed.

## Confinement Violation in Procedure Call

**Listing 8.2:** *Confinement violation in capsule procedure call*
``` java
class TestC {
  TestC next;
  void setNext(TestC next) { this.next = next; }
}

capsule C {
  void test(TestC tc) {  }
}

capsule M (C c) {
  TestC tc = new TestC();
  void mtest() {
    tc.setNext(tc);
    c.test(tc);
  }
}

capsule ConfineTest {
  design {
    C c; M m;
    m(c);
  }

  void run() {
    m.mtest();
  }
}
```
{: .code-with-line-numbers}


## Confinement Violation in Return Statements

**Listing 8.3:** *Confinement violation in return statements*

``` java
class TestC {
  TestC next;
  void setNext(TestC next) { this.next = next; }
}

capsule M () {
  TestC tc = new TestC();

  TestC mtest2() {
    return tc;
  }
}

capsule ConfineTest {
  design {
    M m;
    m();
  }

  void run() {
    m.mtest2();
  }
}
```
{: .code-with-line-numbers}


## Resolving Confinement Violation

We suggest three strategies for resolving a confinement warning to the Panini
programmer: (1) create a clone of the object, (2) if access to the entire object
is not needed pass a portion of the object, and (3) if the object is large and
shared, create a shared capsule whose sole purpose is to encapsulate that large
object. In the rest of this section, we illustrate these strategies.

### Resolving confinement violation using cloning

**Listing 8.4:** *Resolving confinement violation with clones*

**TODO:** A complete example that compiles and runs here. Resolving confinement
violation by passing parts


**Listing 8.5:** *Resolving confinement violation by passing parts*

**TODO:** A complete example that compiles and runs here. Resolving confinement
violation by creating shared capsules


**Listing 8.6:** *Resolving confinement violation by creating shared capsules.*

**TODO:** A complete example that compiles and runs here.
