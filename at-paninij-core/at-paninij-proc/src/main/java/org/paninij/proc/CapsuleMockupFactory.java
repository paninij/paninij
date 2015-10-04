package org.paninij.proc;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.joining;
import static org.paninij.proc.util.Source.cat;
import static org.paninij.proc.util.Source.format;
import static org.paninij.proc.util.Source.formatAligned;
import static org.paninij.proc.util.Source.formatAll;
import static org.paninij.proc.util.Source.lines;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.paninij.proc.model.AnnotationKind;
import org.paninij.proc.model.Capsule;
import org.paninij.proc.model.Procedure;
import org.paninij.proc.model.Variable;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.Source;

public class CapsuleMockupFactory extends SignatureArtifactFactory
{
    public static final String CAPSULE_MOCKUP_SUFFIX = "$Mockup";

    
    @Override
    protected String getQualifiedName()
    {
        return signature.getQualifiedName() + CAPSULE_MOCKUP_SUFFIX;
    }
    
    protected String getSimpleName()
    {
        return signature.getSimpleName() + CAPSULE_MOCKUP_SUFFIX;
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
                "@CapsuleMockup",
                "public class #1 extends Capsule$Mockup implements #2",
                "{",
                "    ##",
                "",
                "    ##",
                "}");

        src = format(src, this.signature.getPackage(),
                          this.getSimpleName(),
                          this.signature.getSimpleName());

        src = formatAligned(src, this.generateImports());
        src = formatAligned(src, this.generateImportsMethod());
        src = formatAligned(src, this.generateProcedures());

        return src;
    }


    protected List<String> generateImports()
    {
        Set<String> imports = this.signature.getImports();
        imports.add("org.paninij.runtime.Capsule$Mockup");
        imports.add("org.paninij.lang.CapsuleMockup");

        //1.7 compliant
        List<String> importList = new ArrayList<String>();
        for(String imp : imports)
        {
        	importList.add("import " + imp + ";");
        }
        return importList;
        
        //1.8 alternative
        /*
        return imports.stream()
                      .map(i -> "import " + i + ";")
                      .collect(toList());
        */
    }


    protected List<String> generateImportsMethod()
    {
        if (signature instanceof Capsule)
        {
            List<String> decls = new ArrayList<String>();
            for (Variable v : ((Capsule) this.signature).getImportFields()) {
                decls.add(v.toString());
            }

            if (decls.isEmpty()) {
                return lines();
            }
            else
            {
                List<String> src = lines("public void imports(#0)",
                                         "{",
                                         "    /* Do nothing. */",
                                         "}");
                return Source.formatAll(src, String.join(", ", decls));
            }
        }
        else {
            return Source.lines();  // Return an empty list.
        }
    }


    protected List<String> generateProcedures()
    {
        List<String> src = new ArrayList<String>();
        for (Procedure p : signature.getProcedures()) {
            src.addAll(this.generateProcedure(p));
        }
        return src;
    }

    
    protected List<String> generateProcedure(Procedure procedure)
    {
        MessageShape shape = new MessageShape(procedure);
        
        //1.7 compliant
        String params = "";
        List<String> varStrings = new ArrayList<String>();
        for(Variable var : procedure.getParameters())
        {
        	varStrings.add(var.toString());
        }
        params = String.join(",", varStrings);
        
        //1.8 alternative
        /*
        String params = procedure.getParameters()
                                 .stream()
                                 .map(v -> v.toString())
                                 .collect(joining(", "));
		*/
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
