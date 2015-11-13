
package org.paninij.proc.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.paninij.proc.PaniniProcessor;
import org.paninij.proc.model.Procedure;
import org.paninij.proc.model.Signature;
import org.paninij.proc.model.Variable;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.Source;
import org.paninij.proc.util.SourceFile;

public class SignatureFactory implements ArtifactFactory<Signature>
{
    Signature signature;
	
    @Override
    public SourceFile make(Signature signature)
    {
        this.signature = signature;
        return new SourceFile(this.getQualifiedName(), this.generateContent());
    }
    
    protected String getQualifiedName()
    {
        return signature.getQualifiedName();
    }
	
    protected String generateContent() {
        String src = Source.cat(
                "package #0;",
                "",
                "##",
                "",
                "#1",
                "@SuppressWarnings(\"unused\")",  // To suppress unused import warnings.
                "@SignatureInterface",
                "public interface #2",
                "{",
                "    ##",
                "}");

        src = Source.format(src,
                this.signature.getPackage(),
                PaniniProcessor.getGeneratedAnno(SignatureFactory.class),
                this.signature.getSimpleName());
        src = Source.formatAligned(src, this.generateImports());
        src = Source.formatAligned(src, this.generateFacades());

        return src;
    }

    protected List<String> generateImports() {
        Set<String> imports = new HashSet<String>();
        imports.add("org.paninij.lang.SignatureInterface");
        imports.add("javax.annotation.Generated");

        for (Procedure p : this.signature.getProcedures()) {
            MessageShape shape = new MessageShape(p);
            imports.add(shape.getPackage() + "." +shape.encoded);
        }

        imports.addAll(this.signature.getImports());

        List<String> prefixedImports = new ArrayList<String>();

        for (String i : imports) {
            prefixedImports.add("import " + i + ";");
        }

        return prefixedImports;
    }

    protected List<String> generateFacades() {
        List<String> facades =  new ArrayList<String>();

        for (Procedure p : this.signature.getProcedures()) {
            facades.add(this.generateFacade(p));
            facades.add("");
        }

        return facades;
    }

    protected String generateFacade(Procedure p) {
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
