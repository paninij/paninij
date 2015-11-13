
package org.paninij.proc.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.paninij.proc.PaniniProcessor;
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
                "}");

        src = Source.format(src,
                this.capsule.getPackage(),
                PaniniProcessor.getGeneratedAnno(CapsuleInterfaceFactory.class),
                this.capsule.getSimpleName(),
                this.generateInterfaces(),
                this.generateImportDecl());

        src = Source.formatAligned(src, this.generateImports());
        src = Source.formatAligned(src, this.generateFacades());

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

        String declaration = Source.format("public #0 #1(#2);",
                shape.realReturn,
                p.getName(),
                argDeclString);

        return declaration;
    }
}
