---
part: 2
chapter: 6
title: Design Declarations
---

A capsule declaration may optionally contain a design declaration. A design
declaration is a declarative static specification of the topology of capsule
instances that would be internal to that capsule. The topology of these capsule
instances is fixed for a capsule declaration and does not change dynamically,
which facilitates more precise static analysis of capsule interactions. Arrays
of capsule instances of fixed length can also be declared.

For our banking example a design declaration could look like:

**Listing 6.1:** *Design Declaration in Bank Capsule*

``` java
capsule Bank {
 design {
  /* these two lines specify the capsule instances
     that will be participating in the design */
  BankAccount account;
  BankClient client;

  /* this line describes how the capsule instances
     are connected with each other */
  client(account);
 }
}
```

This design declaration spans lines 2-11. On line 5 and 6 this design
declaration specifies parts (or internal components) of the capsule Bank and on
line 10 it says how these internal components are connected to each other.


## Capsule Instance Declarations

You have already seen capsule instance declaration in the previous design
example, where we declared a BankAccount instance and a BankClient capsule
instance on lines 5 and 6 respectively. We do not need to explicitly allocate
memory for a capsule instance with a new-like operator as this is handled
implicitely.


## Wiring Capsule Instances

A capsule instance can neither be returned nor passed as arguments to class
methods (capsule instances are not first-class values); informally, capsule
instances can only be wired or connected to other capsules.

Let us look at a BankClient capsule that wants to make use of a BankAccount:

``` java
/* the BankClient depends on the signature BankAccountSig */
capsule BankClient (BankAccountSig account) {
  void run() {
    account.withdraw(10);
  }
}
```

Two checks are performed on the design declaration to determine if all capsule
instances are being properly wired.

First check disallows "null" values to be wired as capsule instances. For
example, in the following capsule-oriented program the wiring declaration on
line 6 is illegal.

``` java
Capsule C (C c, int i){}
capsule Test {
 design {
  C c;
  C c1;
  c(null, 0);   //is illegal
  c(c1, 0);  //legal
 }
}
```

The second check ensures that any capsule with arguments are properly wired. For
example, in the following capsule-oriented program the wiring declarations are
incomplete.

``` java
Capsule C{}
Capsule D(int i){}
capsule Test {
 design {
  C c;
  D d;
 }
}
```

So the compiler will report an error error: Capsule instance d may not be
correctly initialized. since the capsule instance d on line 6 expects an initial
integer value and it has not been provided.


### Arrays of Capsule Instances

In effort to allow for even further code reuse, capsule arrays may be
instantiated. The syntax is the same as Java’s.

``` java
capsule Bank{
 design {
  BankAccount accounts[5];
  BankClient client[5];

  for (int i = 0; i &lt; accounts.length; i++) {
    client[i](accounts[i]);
  }
 }
}
```

## Topology operators in design declarations

To make writing complex design declarations easier and to decrease tedious,
repetitive wiring declarations in large designs, Panini provides some topology
operators. These are: `wireall`, `ring`, `assoc`, and `star`.

These operators simplify wiring some of the common type of connections between
capsules.

The `wireall` operator connects each element in a capsule array to the same set
of arguments. For example, if `cs` is an array of capsule instances, of length n
writing

``` java
wireall(cs, arg1, arg2, ...);
```

is the same as writing the following n wiring declarations.

``` java
cs[0](arg1, arg2, ...);
cs[1](arg1, arg2, ...);
...
cs[n-1](arg1, arg2, ...);
```

The ring operator connects each element ’N’ in a capsule array to element ’N+1’.
It also connects the last element in the capsule array to the first element in
the array. For example, if cs is an array of capsule instances, of length n,
then writing

``` java
ring(cs);
``` java

is the same as writing the following n wiring declarations.

``` java
cs[0](cs[1]);
cs[1](cs[2]);
...
cs[n-1](cs[0]);
```

The `assoc` (short for associate) operator connects elements of two capsule
arrays from a starting index i, for a l items. For example, if cs and ds are
arrays of capsule instances of length >=4, then writing

``` java
assoc(cs, 3, ds, 2, args);
```

is the same as writing the following two wiring declarations.

``` java
cs[3](ds[3], args);
cs[4](ds[4], args);
```

The `star` operator connects a single capsule instance to all elements of a
capsule array. For example, if c is a capsule instance and cs is an array of
capsule instances following wires c to all elements of ds.

``` java
star(c, cs, args);
```
