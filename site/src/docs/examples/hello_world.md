---
layout: page
title: Hello World Example
---

In this example, we have three capsules, the first is a passive capsule named
Console, the second is a passive capsule named Greeter and the third is an
active capsule called HelloWorld.

<div class="row">
<div class="col-md-10 offset-md-1">
<img src="/img/hello_world_capsule_system.jpg"
     class="img-fluid"
     alt="The Hello World Capsule System">
</div>
</div>
<br />


### ConsoleTemplate.java

``` java
import org.paninij.lang.*;

@Capsule
public class ConsoleTemplate {

  @Block
  public void write(String s) {
    System.out.println(s);
  }
}
```

The Console capsule has one simple procedure called write. It simply prints out
to the System.out stream.

### GreeterTemplate.java

``` java
import org.paninij.lang.*;

@Capsule
public class GreeterTemplate {

  @Imports Console c;
  String message;

  void init() {
    message = "Hello";
  }

  @Block
  public void sendMessage() {
    c.write(message);
  }
}
```

The capsule Greeter has a field of type Console that is annotated with the
`@Imports`. This means that the Greeter wants to call procedures of a Console
capsule, but does not want to create the instance of the capsule. By using
`@Imports`, it specifies that the parent capsule of the Greeter must supply the
reference to the Console capsule. This is shown in the HelloWorldTemplate in the
design method where g.imports is called and the reference to the Console capsule
is passed as a parameter.

### HelloWorldTemplate.java

``` java
import org.paninij.lang.*;

@Root
@Capsule
public class HelloWorldTemplate {
  @Local Greeter g;
  @Local Console c;

  void design(HelloWorld self) {
   g.imports(c);
  }

  void run() {
    g.sendMessage();
  }

  public static void main(String[] args) {
    CapsuleSystem.start(HelloWorld.class, args);
  }
}
```

The active capsule HelloWorld holds a reference to the passive capsules Greeter
and Console in order to call on their procedures. This connection is set up by
the `@Local` annotation on the Greeter and Console field of the
HelloWorldTemplate.
