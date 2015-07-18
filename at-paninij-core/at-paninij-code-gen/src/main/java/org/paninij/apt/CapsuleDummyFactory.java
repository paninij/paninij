package org.paninij.apt;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.joining;
import static org.paninij.apt.util.Source.cat;
import static org.paninij.apt.util.Source.format;
import static org.paninij.apt.util.Source.formatAll;
import static org.paninij.apt.util.Source.formatAligned;
import static org.paninij.apt.util.Source.lines;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.paninij.apt.util.MessageShape;
import org.paninij.apt.util.Source;
import org.paninij.model.AnnotationKind;
import org.paninij.model.Procedure;
import org.paninij.model.Variable;

public class CapsuleDummyFactory extends CapsuleArtifactFactory
{
    public static final String CAPSULE_DUMMY_SUFFIX = "$Dummy";

    
    @Override
    protected String getQualifiedName()
    {
        return capsule.getQualifiedName() + CAPSULE_DUMMY_SUFFIX;
    }
    
    protected String getSimpleName()
    {
        return capsule.getSimpleName() + CAPSULE_DUMMY_SUFFIX;
    }
    
    @Override
    protected String generateContent()
    {
        String src = cat(
                "package #0;",
                "",
                "##",
                "",
                "@SuppressWarnings(\"unused\")",  // To suppress unused import warnings.
                "@CapsuleDummy",
                "public class #1 extends Capsule$Dummy implements #2",
                "{",
                "    ##",
                "",
                "    ##",
                "}");

        src = format(src, this.capsule.getPackage(),
                          this.getSimpleName(),
                          this.capsule.getSimpleName());

        src = formatAligned(src, this.generateImports());
        src = formatAligned(src, this.generateWiredMethod());
        src = formatAligned(src, this.generateProcedures());

        return src;
    }


    protected List<String> generateImports()
    {
        Set<String> imports = this.capsule.getImports();
        imports.add("org.paninij.runtime.Capsule$Dummy");
        imports.add("org.paninij.lang.CapsuleDummy");
        return imports.stream()
                      .map(i -> "import " + i + ";")
                      .collect(toList());
    }


    protected List<String> generateWiredMethod()
    {
        List<String> decls = new ArrayList<String>();

        for (Variable v : this.capsule.getWired()) {
            decls.add(v.toString());
        }

        if (decls.isEmpty())
        {
            return lines();
        }
        else
        {
            List<String> src = lines("public void wire(#0)",
                                     "{",
                                     "    /* Do nothing. */",
                                     "}");
            return Source.formatAll(src, String.join(", ", decls));
        }
    }


    protected List<String> generateProcedures()
    {
        List<String> src = new ArrayList<String>();
        for (Procedure p : capsule.getProcedures()) {
            src.addAll(this.generateProcedure(p));
        }
        return src;
    }

    
    protected List<String> generateProcedure(Procedure procedure)
    {
        MessageShape shape = new MessageShape(procedure);
        String params = procedure.getParameters()
                                 .stream()
                                 .map(v -> v.toString())
                                 .collect(joining(", "));

        List<String> src = lines("@Override",
                                 "public #0 #1(#2) {",
                                 "    /* Do Nothing */",
                                 "    #3",
                                 "}",
                                 "");

        return formatAll(src, shape.realReturn,
                              procedure.getName(),
                              params,
                              generateProcedureReturnStatement(procedure));
    }
    

    protected String generateProcedureReturnStatement(Procedure procedure)
    {
        if (procedure.getAnnotationKind() == AnnotationKind.FUTURE)
        {
            return "return null;";
        }

        switch (procedure.getReturnType().getKind())
        {
        case VOID:
            return "";

        case ARRAY:
        case DECLARED:
            return "return null;";

        case BOOLEAN:
            return "return false;";

        case CHAR:
            return "return '\0'";

        case BYTE:
        case SHORT:
        case LONG:
        case DOUBLE:
        case FLOAT:
        case INT:
            return "return 0;";

        case INTERSECTION:
        case EXECUTABLE:
        case NONE:
        case NULL:
        case ERROR:
        case OTHER:
        case PACKAGE:
        case TYPEVAR:
        case UNION:
        case WILDCARD:
        default:
            throw new UnsupportedOperationException();
        }
    }
}
