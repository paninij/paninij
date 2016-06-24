# Developing the @PaniniJ Eclipse Plugin

## How to Start Developing within Eclipse

1. Generate an Eclipse project: `./gradlew clean :jdt:plugin:eclipse`
2. Open the Eclipse Plugin Development Environment (PDE).
3. Import the just-generated Eclipse project in this directory.


## Development Notes

Because of the current relative lack of support for developing and deploying
Eclipse plugins, Gradle is not involved in these tasks for this plugin.
Instead, this plugin is meant to be developed and deployed from within the
Eclipse IDE. However, Gradle is used to (somewhat) aid in simplifying the
build and making it reproducible in two ways:

1. Gradle copies those non-OSGi-Bundle dependencies upon which this plugin
depends by copying them into this project's `libs` folder.
2. The Gradle `eclipse` plugin is configured to generate certain standard
Eclipse project files (e.g. `.project` and `.classpath`) to be used by in-IDE
development.


## The `libs/` Directory

The `:jdt:plugin:libs` task copies those dependencies of `:jdt:plugin` which
are not already packaged as Eclipse Plugins (or more precisely as OSGi
Bundles). These dependency JARs are copied into this sub-project's `libs/`
directory to both serve as a dependency during development and also included
within the plugin JAR itself for use at runtime.

This may seem like a somewhat strange build strategy (especially in comparison
to the relative simplicity of Maven- and Gradle-style transitive dependencies).
However, it is necessary to work around the inherent incompatibilities between
standard Maven/Gradle build practices and Eclipse Plugin (i.e. OSGi Bundle)
dependency . In particular, this inclusion of dependency JARs prevents the
extra complexity of packaging and publishing this plugin's dependencies as
both Maven artifacts and Eclipse Plugins.

Note that if any `libs/` directory dependency jars are renamed, added, or
removed, then this change likely needs to be made in both the Gradle
configuration (e.g. `build.gradle`) and the Eclipse plugin configuration (e.g.
`build.properties` and `MANIFEST.MF`).


## Use Gradle to Build Eclipse Project Files

This project uses the `eclipse` plugin to build a basic Eclipse project files
for in-IDE development of this Eclipse plugin. The Gradle `:jdt:plugin:eclipse`
task can be used to generate these files. For both simplicity and build
reproducibility, *it is strongly recommended that this method is used to
generate Eclipse project files for developing this plugin, rather than
generating them in-IDE.*

**Note:** When the `:jdt:plugin:eclipse` task is executed, any pre-existing
config files will clobbered.


### A Note on Buildship

Gradle Buildship is a collection of Eclipse plugins which provide Gradle
support from within Eclipse. Use of Buildship within Eclipse is strongly
recommended for any `@PaniniJ` developer who wants to use Eclipse to developing
`@PaniniJ` in general.

While Buildship currently provides good support for integrating Eclipse and
Gradle for most "normal" Java builds, its features do not specifically aid in
the development of Eclipse plugins. Buildship is therefore not currently very
useful for developing this particular `@PaniniJ` subproject.
