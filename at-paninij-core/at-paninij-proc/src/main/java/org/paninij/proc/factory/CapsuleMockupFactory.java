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

import static org.paninij.proc.util.Source.cat;
import static org.paninij.proc.util.Source.format;
import static org.paninij.proc.util.Source.formatAligned;
import static org.paninij.proc.util.Source.formatAll;
import static org.paninij.proc.util.Source.lines;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.paninij.proc.PaniniProcessor;
import org.paninij.proc.model.AnnotationKind;
import org.paninij.proc.model.Capsule;
import org.paninij.proc.model.Procedure;
import org.paninij.proc.model.Signature;
import org.paninij.proc.model.Variable;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.Source;
import org.paninij.proc.util.SourceFile;

// TODO: Also implement ArtifactFactory<Capsule>
public class CapsuleMockupFactory implements ArtifactFactory<Signature>
{
    public static final String CAPSULE_MOCKUP_SUFFIX = "$Mockup";
    
    Signature signature;

    public SourceFile make(Signature signature)
    {
        this.signature = signature;
        return new SourceFile(this.getQualifiedName(), this.generateContent());
    }
    
    protected String getQualifiedName()
    {
        return signature.getQualifiedName() + CAPSULE_MOCKUP_SUFFIX;
    }
    
    protected String getSimpleName()
    {
        return signature.getSimpleName() + CAPSULE_MOCKUP_SUFFIX;
    }
    
    protected String generateContent()
    {
        String src = cat(
                "package #0;",
                "",
                "##",
                "",
                "#1",
                "@SuppressWarnings(\"unused\")",  // To suppress unused import warnings.
                "@CapsuleMockup",
                "public class #2 extends Capsule$Mockup implements #3",
                "{",
                "    ##",
                "",
                "    ##",
                "}");

        src = format(src,
        		this.signature.getPackage(),
        		PaniniProcessor.getGeneratedAnno(CapsuleMockupFactory.class),
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
        imports.add("javax.annotation.Generated");
        imports.add("org.paninij.runtime.Capsule$Mockup");
        imports.add("org.paninij.lang.CapsuleMockup");

        List<String> importList = new ArrayList<String>();
        for(String imp : imports)
        {
        	importList.add("import " + imp + ";");
        }
        return importList;
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
        
        List<String> varStrings = new ArrayList<String>();
        for(Variable var : procedure.getParameters())
        {
        	varStrings.add(var.toString());
        }
        String params = String.join(",", varStrings);
        
        List<String> src = lines("#4",
                                 "@Override",
                                 "public #0 #1(#2) {",
                                 "    /* Do Nothing */",
                                 "    #3",
                                 "}",
                                 "");

        return formatAll(src, shape.realReturn,
                              procedure.getName(),
                              params,
                              generateProcedureReturnStatement(procedure),
                              shape.kindAnnotation);
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
