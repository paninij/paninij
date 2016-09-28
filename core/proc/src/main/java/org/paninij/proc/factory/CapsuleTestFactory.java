/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/

package org.paninij.proc.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.paninij.proc.model.Procedure;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.Source;

@Deprecated
public class CapsuleTestFactory extends AbstractCapsuleFactory
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
                ArtifactFactory.getGeneratedAnno(CapsuleTestFactory.class),
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
