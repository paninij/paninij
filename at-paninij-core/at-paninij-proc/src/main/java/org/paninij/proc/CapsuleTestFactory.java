/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills, David Johnston
 */
package org.paninij.proc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.paninij.proc.model.Procedure;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.Source;

public class CapsuleTestFactory extends CapsuleArtifactFactory
{
    public static final String CAPSULE_TEST_SUFFIX = "$Test";

    protected String getQualifiedName()
    {
        return this.capsule.getQualifiedName() + CAPSULE_TEST_SUFFIX;
    }

    protected String generateContent()
    {
        String src = Source.cat(
                "package #0;",
                "",
                "##",
                "",
                "#1",
                "public class #2",
                "{",
                "    ##",
                "}");

        src = Source.format(src,
                this.capsule.getPackage(),
                PaniniProcessor.getGeneratedAnno("CapsuleTestFactory"),
                this.generateClassName());
        src = Source.formatAligned(src, generateImports());
        src = Source.formatAligned(src, generateTests());

        return src;
    }

    private String generateClassName() 
    {
        return this.capsule.getSimpleName() + CAPSULE_TEST_SUFFIX;
    }

    private List<String> generateImports()
    {
        Set<String> imports = new HashSet<String>();

        for (Procedure p : this.capsule.getProcedures()) {
            MessageShape shape = new MessageShape(p);
            imports.add(shape.fullLocation());
        }

        imports.addAll(this.capsule.getImports());
        
        imports.add("javax.annotation.Generated");
        imports.add("java.util.concurrent.TimeUnit");
        imports.add("org.junit.Test");
        imports.add("org.paninij.runtime.Capsule$Thread");
        imports.add("org.paninij.runtime.SimpleMessage");
        imports.add("org.paninij.runtime.Panini$Message");
        imports.add(this.capsule.getQualifiedName());

        List<String> prefixedImports = new ArrayList<String>();

        for (String i : imports) {
            prefixedImports.add("import " + i + ";");
        }

        return prefixedImports;
    }

    private List<String> generateTests()
    {
        List<String> src = Source.lines();
        int testId = 0;
        for (Procedure procedure : this.capsule.getProcedures()) {
            src.addAll(this.generateTest(procedure, testId++));
            src.add("");
        }
        return src;
    }

    private List<String> generateTest(Procedure procedure, int testId)
    {
        List<String> src = Source.lines(
                "@Test",
                "public void #0() throws Throwable {",
                "    Panini$Message test_msg = new SimpleMessage(#1);",
                "    Panini$Message exit_msg = new SimpleMessage(Capsule$Thread.PANINI$TERMINATE);",
                "",
                "    #2#3 capsule = new #2#3();",
                "    capsule.panini$push(test_msg);",
                "    capsule.panini$push(exit_msg);",
                "    capsule.run();",
                "",
                "    // Re-throw any errors that were caught by `capsule`.",
                "    Throwable thrown = capsule.panini$pollErrors(1, TimeUnit.SECONDS);",
                "    if (thrown != null) {",
                "        throw thrown;",
                "    }",
                "}");
        return Source.formatAll(src, procedure.getName(),
                                     testId,
                                     this.capsule.getSimpleName(),
                                     CapsuleThreadFactory.CAPSULE_PROFILE_THREAD_SUFFIX);
    }
}
