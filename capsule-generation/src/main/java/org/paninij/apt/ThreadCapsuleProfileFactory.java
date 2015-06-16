package org.paninij.apt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.paninij.apt.util.MessageShape;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;
import org.paninij.apt.util.SourceFile;
import org.paninij.model.Capsule;
import org.paninij.model.Procedure;
import org.paninij.model.Variable;

public class ThreadCapsuleProfileFactory extends CapsuleProfileFactory
{

    public static final String CAPSULE_PROFILE_THREAD_SUFFIX = "$Thread$Temp";

    private Capsule context;

    @Override
    public SourceFile make(Capsule capsule) {
        this.context = capsule;

        String name = this.generateFileName();
        String content = this.generateContent();

        return new SourceFile(name, content);
    }

    private String generateFileName() {
        return this.context.getQualifiedName() + CAPSULE_PROFILE_THREAD_SUFFIX;
    }

    private String generateContent() {
        String src = Source.cat(
                "package #0;",
                "",
                "##",
                "",
                "public class #1 extends Capsule$Thread implements #2",
                "{",
                "    ##",
                "}");

        src = Source.format(src,
                this.context.getPackage(),
                this.generateClassName(),
                this.context.getSimpleName());

        src = Source.formatAligned(src, generateImports());
        src = Source.formatAligned(src, generateCapsuleBody());

        return src;
    }

    private String generateClassName() {
        return this.context.getSimpleName() + CAPSULE_PROFILE_THREAD_SUFFIX;
    }

    private List<String> generateImports() {
        Set<String> imports = new HashSet<String>();

        for (Procedure p : this.context.getProcedures()) {
            MessageShape shape = new MessageShape(p);
            imports.add(shape.getPackage() + "." +shape.encoded);
        }

        imports.addAll(this.context.getImports());

        imports.add("java.util.concurrent.Future");
        imports.add("org.paninij.runtime.Capsule$Thread");
        imports.add(this.context.getQualifiedName());

        List<String> prefixedImports = new ArrayList<String>();

        for (String i : imports) {
            prefixedImports.add("import " + i + ";");
        }

        return prefixedImports;
    }

    private String generateEncapsulatedDecl() {
        return Source.format(
                "private #0 panini$encapsulated = new #0();",
                this.context.getQualifiedName() + PaniniModelInfo.CAPSULE_TEMPLATE_SUFFIX);
    }

    private List<String> generateProcedureIDs() {
        ArrayList<String> decls = new ArrayList<String>();
        int currID = 0;

        for (Procedure p : this.context.getProcedures()) {
            decls.add(Source.format("public static final int #0 = #1;",
                    generateProcedureID(p),
                    currID++));
        }

        return decls;
    }

    private String generateProcedureID(Procedure p) {
        String base = "panini$proc$";
        List<String> params = new ArrayList<String>();

        for (Variable param : p.getParameters()) {
            params.add(param.encodeFull());
        }

        String paramStrings = params.size() > 0 ? "$" + String.join("$", params) : "";

        return base + p.getName() + paramStrings;
    }

    private List<String> generateCapsuleBody() {
        List<String> src = new ArrayList<String>();

        src.add(this.generateEncapsulatedDecl());
        src.addAll(this.generateProcedureIDs());

        return src;
    }

}
