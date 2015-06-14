package org.paninij.apt;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.TypeElement;

import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;
import org.paninij.model.Capsule;

public class MakeCapsuleTester$Thread extends MakeCapsule$Thread
{
    private static final String EXEC_TYPE_SUFFIX = "$Thread";
    
    static MakeCapsuleTester$Thread make(PaniniProcessor context, TypeElement template, Capsule capsule)
    {
        MakeCapsuleTester$Thread cap = new MakeCapsuleTester$Thread();
        cap.context = context;
        cap.template = template;
        cap.capsule = capsule;
        return cap;
    }


    @Override
    List<String> buildMain()
    {
        // Every capsule tester should have a `main()` method. For each test it defines, a fresh
        // `tester` capsule should be instantiated to run that test and no others.
        
        // TODO: Fix this especially ugly hack:
        int num_tests = buildProcedureIDs().size();
        
        List<String> src = Source.lines("public static void main(String[] args)",
                                        "{",
                                        "    Panini$Message exit_msg;",
                                        "    Panini$Message test_msg;",
                                        "",
                                        "    for (int test_id = 0; test_id < #1; test_id++)",
                                        "    {",
                                        "        System.err.println(\"Running test \" + test_id);",
                                        "        #0 tester = new #0();",
                                        "        test_msg = new SimpleMessage(test_id);",
                                        "        exit_msg = new SimpleMessage(PANINI$SHUTDOWN);",
                                        "        tester.panini$push(test_msg);",
                                        "        tester.panini$push(exit_msg);",
                                        "        tester.run();",
                                        "        System.err.println();",
                                        "    }",
                                        "",
                                        "    System.exit(0);",
                                        "}");

         return Source.formatAll(src, buildCapsuleName(), num_tests);
    }
    
    @Override
    String buildCapsuleName() {
        return PaniniModelInfo.simpleTesterName(template) + EXEC_TYPE_SUFFIX;
    }
    
    @Override
    String buildQualifiedCapsuleName() {
        return PaniniModelInfo.qualifiedTesterName(template) + EXEC_TYPE_SUFFIX;
    }

    @Override
    String buildCapsuleDecl() {
        return Source.format("public class #0 extends Capsule$Thread", buildCapsuleName());
    }

    @Override
    Set<String> getStandardImports()
    {
        Set<String> imports = super.getStandardImports();
        imports.add("org.paninij.runtime.SimpleMessage");
        return imports;
    }
}
