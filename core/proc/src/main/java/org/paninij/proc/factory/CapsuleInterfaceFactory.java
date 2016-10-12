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
 *  Jackson Maddox
 *******************************************************************************/

package org.paninij.proc.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.paninij.proc.model.Procedure;
import org.paninij.proc.model.Variable;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.Source;

public class CapsuleInterfaceFactory extends AbstractCapsuleFactory
{
    @Override
    protected String getQualifiedName()
    {
        return capsule.getQualifiedName();
    }
    
    @Override
    protected String generateContent()
    {
        String src = Source.cat(
                "package #0;",
                "",
                "##",
                "",
                "#1",
                "@SuppressWarnings(\"unused\")",  // To suppress unused import warnings.
                "@CapsuleInterface",
                "public interface #2 extends #3",
                "{",
                "    #4",
                "    ##",
                "    ##",
                "}");

        src = Source.format(src,
                this.capsule.getPackage(),
                ArtifactFactory.getGeneratedAnno(CapsuleInterfaceFactory.class),
                this.capsule.getSimpleName(),
                this.generateInterfaces(),
                this.generateImportDecl());

        src = Source.formatAligned(src, this.generateImports());
        src = Source.formatAligned(src, this.generateFacades());
        src = Source.formatAligned(src, this.generateEventFacades());

        return src;
    }

    protected String generateInterfaces()
    {
        List<String> interfaces = this.capsule.getSignatures();
        if (capsule.isRoot()) {
            interfaces.add("Panini$Capsule$Root");
        } else {
            interfaces.add("Panini$Capsule");
        }
        return String.join(", ", interfaces);
    }

    protected String generateImportDecl()
    {
        List<String> decls = new ArrayList<String>();

        for (Variable v : this.capsule.getImportFields()) {
            decls.add(v.toString());
        }

        return decls.isEmpty() ? "" : Source.format("public void imports(#0);", String.join(", ", decls));
    }

    protected List<String> generateImports()
    {
        Set<String> imports = new HashSet<String>();

        for (Procedure p : this.capsule.getProcedures()) {
            MessageShape shape = new MessageShape(p);
            imports.add(shape.fullLocation());
        }

        imports.addAll(this.capsule.getImports());
        imports.add("javax.annotation.Generated");
        imports.add("java.util.concurrent.Future");
        imports.add("org.paninij.lang.CapsuleInterface");
        imports.add("org.paninij.runtime.Panini$Capsule");
        imports.add("org.paninij.runtime.Panini$Capsule$Root");
        imports.add("org.paninij.lang.PaniniEventExecution");
        
        List<String> prefixedImports = new ArrayList<String>();

        for (String i : imports) {
            prefixedImports.add("import " + i + ";");
        }

        return prefixedImports;
    }

    protected List<String> generateFacades()
    {
        List<String> facades =  new ArrayList<String>();

        for (Procedure p : this.capsule.getProcedures()) {
            facades.add(this.generateFacade(p));
            facades.add("");
        }
        for (Procedure p : this.capsule.getEventHandlers()) {
            facades.add(this.generateHandlerFacades(p));
            facades.add("");
        }

        return facades;
    }

    protected String generateFacade(Procedure p)
    {
        MessageShape shape = new MessageShape(p);

        List<String> argDecls = new ArrayList<String>();

        for (Variable v : p.getParameters()) {
            argDecls.add(v.toString());
        }

        String argDeclString = String.join(", ", argDecls);

        String declaration = Source.format("#3 public #0 #1(#2);",
                shape.realReturn,
                p.getName(),
                argDeclString,
                shape.kindAnnotation);

        return declaration;
    }
    
    protected String generateHandlerFacades(Procedure p) {
        Variable param = p.getParameters().get(0);
        String argDeclString = param.toString();
        String declaration = Source.format("public void #0(PaniniEventExecution<#2> ex, #1);", 
                p.getName(),
                argDeclString,
                param.getMirror().toString());
        
        return declaration;
    }
    
    protected List<String> generateEventFacades() {
        List<String> facades = new ArrayList<>();

        List<Variable> allEvents = capsule.getBroadcastEventFields();
        allEvents.addAll(capsule.getChainEventFields());
        
        for (Variable v : allEvents) {
            facades.add(Source.format("public #0 #1();",
                    v.raw(),
                    v.getIdentifier()));
        }
        return facades;
    }
}
