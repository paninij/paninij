package org.paninij.apt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.TypeElement;

import org.paninij.apt.util.MessageShape;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;
import org.paninij.model.Capsule;
import org.paninij.model.Procedure;

public class MakeCapsuleTest
{
    protected PaniniProcessor context;
    protected TypeElement template;
    
    static MakeCapsuleTest make(PaniniProcessor context, TypeElement template)
    {
        MakeCapsuleTest cap = new MakeCapsuleTest();
        cap.context = context;
        cap.template = template;
        return cap;
    }

    void makeSourceFile()
    {
        String capsuleName = PaniniModelInfo.qualifiedTesterName(template);
        context.createJavaFile(capsuleName, build());
    }
    
    String build() {
        String src = Source.cat("package #0;",
                                "",
                                "##",
                                "",
                                "public class #1",
                                "{",
                                "    ##",
                                "}");
        
        src = Source.format(src, buildPackage(), PaniniModelInfo.simpleTesterName(template));
        src = Source.formatAligned(src, buildImports());
        src = Source.formatAligned(src, buildTests());

        return src;
    }
    
    String buildPackage() {
        return context.getPackageOf(template);
    }

    List<String> buildTests()
    {
        // TODO: Fix this hack. This should be done with names, not numbers.
        List<String> src = Source.lines();
        int num_tests = PaniniModelInfo.getProcedures(template).size();
        for (int idx = 0; idx < num_tests; idx++)
        {
            src.addAll(buildTest(idx));
            src.add("");
        }
        return src;
    }
    

    List<String> buildTest(int test_id)
    {
        // Notice that the instance of the class test in which the tests are run is never used to
        // perform an actual test. A new test instance (`capsule_test`) is generated by each test.
        List<String> src = Source.lines(
                "@Test",
                "public void panini$test#0() throws Throwable",
                "{",
                "    Panini$Message test_msg = new SimpleMessage(#0);",
                "    Panini$Message exit_msg = new SimpleMessage(Capsule$Thread.PANINI$SHUTDOWN);",
                "",
                "    #1$Thread capsule = new #1$Thread();",
                "    capsule.panini$push(test_msg);",
                "    capsule.panini$push(exit_msg);",
                "    capsule.run();",
                "",
                "    // Re-throw any errors that were caught by `capsule`.",
                "    Throwable thrown = capsule.panini$pollErrors(1, TimeUnit.SECONDS);",
                "    if (thrown != null) {",
                "        throw thrown;",
                "    }",
                "}"
        );
        return Source.formatAll(src, test_id, PaniniModelInfo.simpleCapsuleName(template));
    }
    
    List<String> buildImports()
    {
        Set<String> imports = getStandardImports();
        return Source.buildCollectedImportDecls(template, imports);
    }


    Set<String> getStandardImports()
    {
        Set<String> imports = new HashSet<String>();
        imports.add("java.util.concurrent.TimeUnit");
        imports.add("org.junit.Test");
        imports.add("org.paninij.runtime.Capsule$Thread");
        imports.add("org.paninij.runtime.SimpleMessage");
        imports.add("org.paninij.runtime.Panini$Message");
        return imports;
    }
}