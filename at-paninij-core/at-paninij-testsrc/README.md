# Panini Test Source Resources

This project contains various @PaniniJ source files for use in testing the
@PaniniJ tooling, including the annotation processor itself.

This directory is the root of a virtual Java source hierarchy. Thus, classes
defined in this directory are in the default namespace and classes defined in
a `./foo/bar/` are expected to be in the `foo.bar` package.

Many of the classes defined in this hierarchy are in fact *invalid* @PaniniJ
source code. In general, these are indicated via the `@BadInput` annotation.
However, there should not be any invalid *Java* source code in this hierarchy:
all classes should be able to be compiled with a Java 8 compiler. `@BadInput`
thus indicates an input somehow violates some Panini-specific semantic check.

Some of these Java classes have been written as tests for a particular
component in `proc`, and in these cases, there has been an attempt to put the
test class in or under the Java package which contains the *tested* class.
