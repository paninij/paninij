---
title: Setup @PaniniJ in Eclipse
short_title: Eclipse
---

These are the overall steps to setting up an @PaniniJ project in Eclipse:

1. TOC
{:toc}

### Step 1: Setup project to use JRE 1.8 or greater

When you create a new project, be sure to choose JRE 1.8 or greater, this is
necessary for the annotation processing to work correctly.

<div class="row">
<div class="col-md-10 offset-md-1">
<img src="/img/setups/eclipse/step-1.jpg"
     class="img-fluid"
     alt="The Hello World Capsule System">
</div>
</div>


### Step 2: Download the at-paninij jar

Download the @Paninij release from the github releases page. You should have
received two JAR files (proc-0.1.1-SNAPSHOT.jar and lang-0.1.1-SNAPSHOT.jar).

### Step 3: Enable Annotation Processing

Enable annotation processing by right clicking on your project in the project
explorer and choosing "properties." Browse to Java Compiler > Annotation
Processing and check the Enable project specific setting checkbox and Enable
annotation processing.

<div class="row">
<div class="col-md-10 offset-md-1">
<img src="/img/setups/eclipse/step-2.jpg"
     class="img-fluid"
     alt="The Hello World Capsule System">
</div>
</div>

Once you hit Apply, Eclipse will inform you that a rebuild on the project is
required. You can click yes to rebuild the project now.

Note that the "Generated Source Directory" is where the sources that the
annotation processor automatically generates will be stored. You can remove the "."
from ".apt_generated" and it will become visible in eclipse.

### Step 4: Add at-paninij annotation processor

Navigate to the Factory Path section of the project properties. It is beneath
the Annotation Processing option. Check the Enable project specific settings
checkbox, and click the Add External JARs… button.

<div class="row">
<div class="col-md-10 offset-md-1">
<img src="/img/setups/eclipse/step-3.jpg"
     class="img-fluid"
     alt="The Hello World Capsule System">
</div>
</div>

Browse to where you have downloaded the JAR files from step 2. Select both of
the JAR files and click ok. Finally, hit Apply and confirm the project rebuild.

<div class="row">
<div class="col-md-10 offset-md-1">
<img src="/img/setups/eclipse/step-4.jpg"
     class="img-fluid"
     alt="The Hello World Capsule System">
</div>
</div>


### Step 5: Add the at-paninij as a referenced library

The @PaniniJ JAR files includes code necessary for the annotation processing
and runtime. To include the JAR files as a referenced libaray, right click the
project in the project explorer, go to "Build Path", and choose "Add External
Archives…".

<div class="row">
<div class="col-md-10 offset-md-1">
<img src="/img/setups/eclipse/step-5.jpg"
     class="img-fluid"
     alt="The Hello World Capsule System">
</div>
</div>


Browse to where you downloaded the JAR files from step 2, and include them in
your project. Once it is included, the project should appear like this in the
Eclipse project explorer:

<div class="row">
<div class="col-md-10 offset-md-1">
<img src="/img/setups/eclipse/step-6.jpg"
     class="img-fluid"
     alt="The Hello World Capsule System">
</div>
</div>

Now you're ready to start programming in @PaniniJ. See
[this page]({{ site.baseurl }}/docs/examples/hello_world.html)
for an example @PaniniJ Hello World program.
