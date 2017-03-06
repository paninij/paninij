---
title: Setup Eclipse for @PaniniJ Development
short_title: Eclipse
---

1. TOC
{:toc}


### Create a New Java Project

Select "File" > "New" > "Java Project". Name your project. In order for the
compiler plugin to work, be sure to choose JRE 1.8 or greater.

{% include setups/screenshot.html img-src="/img/setups/eclipse/step-1.jpg"
                                  img-alt="Create a New Java Project" %}


### Download the @PaniniJ JARs

Download the latest @Paninij release from our
[GitHub releases page](https://github.com/paninij/paninij/releases). There
should be two JAR files:

- `proc-X.Y.Z.jar`, which includes the annotation processor, that is, the
  compiler plugin. This is only needed at compile time.
- `lang-X.Y.Z.jar`, which includes the runtime. This is needed at both
  compile time and run time.

{% include setups/screenshot.html img-src="/img/setups/eclipse/step-2.jpg"
                                  img-alt="Download the @PaniniJ JARs" %}

In this example, we put these JARs in a `lib/` directory at the root of our
project.


### Enable Annotation Processing

Go to this project's properties by either selecting "Project" > "Properties"
from the top menu bar, or by right clicking on the project and selecting
"Properties".

Then browse to "Java Compiler" > "Annotation Processing". Now check "Enable
project specific settings" and make sure that "Enable annotation processing" is
checked.

{% include setups/screenshot.html img-src="/img/setups/eclipse/step-3.jpg"
                                  img-alt="Enable Annotation Processing" %}

Once you hit "Apply", Eclipse will inform you that a rebuild on the project is
required. You can click "Yes" to rebuild the project now.

Note that the "Generated Source Directory" is where the sources that the
annotation processor automatically generates will be stored. If you remove the
`.` prefix from `.apt_generated`, then this folder and the generated sources
will become visible in the Eclipse file browser.


### Add the @PaniniJ Annotation Processor

Navigate to the "Factory Path" section of the project properties. It is beneath
the "Annotation Processing" option. Check the "Enable project specific settings
checkbox", and click the "Add JARs..." button.

(*Note:* If the @PaniniJ JARs are not nested somewhere within the Eclipse
project, then you will need to use "Add External JARs..." instead.)

{% include setups/screenshot.html img-src="/img/setups/eclipse/step-4a.jpg"
                                  img-alt="Add the Annotation Processor" %}

Browse to where you have placed previously downloaded JAR files. Select both of
the JAR files and click "Ok".

Finally, hit "Apply" and confirm the project rebuild.

{% include setups/screenshot.html img-src="/img/setups/eclipse/step-4b.jpg"
                                  img-alt="Add the Annotation Processor" %}


### Add the @PaniniJ JARs as Dependencies

Lastly, we add the @PaniniJ JAR files as dependencies of our build by adding
them to our "Build Path". To do so, return to this project's properties and
select "Java Build Path". Similar to before, click "Add JARs..." and browse to
the @PaniniJ JARs. Select them both and click "Ok".


{% include setups/screenshot.html img-src="/img/setups/eclipse/step-5a.jpg"
                                  img-alt="Add the JARs as Dependencies" %}

You should see something like this once you have added these to the build path.

{% include setups/screenshot.html img-src="/img/setups/eclipse/step-5b.jpg"
                                  img-alt="Add the JARs as Dependencies" %}

Finally, click "Ok" to exit project properties.

Now you're ready to start programming with @PaniniJ. See
[The Getting Started](/man/p1/ch2_getting_started.html) chapter of the manual
to take the next step.
