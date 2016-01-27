# Panini Test Source Resources

This directory contains @PaniniJ source files for use in testing. The intention is that these only be used as tests inputs while testing `proc` using Google's `compile-testing`.

This directory is the root of a virtual Java source hierarchy. Thus, classes defined in this directory are in the default namespace and classes defined in a `./foo/bar/` are expected to be in the `foo.bar` package.

Many of the classes defined in this hierarchy are in fact *invalid* @PaniniJ source code. In general, these are indicated via the `@BadInput` annotation. However, there should not be any invalid Java source code in this hierarchy: all classes should be able to be compiled with a Java 8 compiler. `@BadInput` thus indicates an input somehow violates some Panini-specific semantic check.

Some of these Java classes have been written as tests for a particular component in `proc`, and in these cases, we've tried to put the test class in or under the Java package containing the *tested* class.