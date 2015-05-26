package org.paninij.apt;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.paninij.apt.util.JavaModelInfo;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;
import org.paninij.model.Procedure;

public class FutureMessageGenerator extends MessageGenerator
{

    public static void generate(PaniniProcessor context, Procedure procedure) {
        FutureMessageGenerator generator = new FutureMessageGenerator();
        String src = generator.generateFuture(procedure);
        try {
            context.createJavaFile(generator.buildQualifiedClassName(procedure), src);
        } catch (UnsupportedOperationException ex) {
            context.warning(ex.toString());
        }
    }

    private String generateFuture(Procedure procedure) {
        String src = Source.cat(
                "package #0;",
                "",
                "##",
                "",
                "public class #1 implements Panini$Message, Panini$Future<#2>, Future<#2>", //TODO drop the panini$future
                "{",
                "    public final int panini$procID;",
                "    private #2 panini$result = null;",
                "    protected boolean panini$isResolved = false;",
                "",
                "    ##",
                "",
                "    ##",
                "",
                "    @Override",
                "    public int panini$msgID() {",
                "        return panini$procID;",
                "    }",
                "",
                "    @Override",
                "    public void panini$resolve(#2 result) {",
                "        synchronized (this) {",
                "            panini$result = result;",
                "            panini$isResolved = true;",
                "            this.notifyAll();",
                "        }",
                "        ##",
                "    }",
                "",
                "    @Override",
                "    public #2 panini$get() {",
                "        while (panini$isResolved == false) {",
                "            try {",
                "                synchronized (this) {",
                "                    while (panini$isResolved == false) this.wait();",
                "                }",
                "            } catch (InterruptedException e) { /* try waiting again */ }",
                "         }",
                "         return panini$result;",
                "    }",
                "",
                "    @Override",
                "    public #2 get() {",
                "        return this.panini$get();",
                "    }",
                "",
                "    @Override",
                "    public #2 get(long timeout, TimeUnit unit)",
                "            throws InterruptedException, ExecutionException, TimeoutException {",
                "        return this.panini$get();",
                "    }",
                "",
                "    @Override",
                "    public boolean isDone() {",
                "        return this.panini$isResolved;",
                "    }",
                "",
                "    @Override",
                "    public boolean cancel(boolean mayInterruptIfRunning) {",
                "        return false;",
                "    }",
                "",
                "    @Override",
                "    public boolean isCancelled() {",
                "        return false;",
                "    }",
                "",
                "}");

        src = Source.format(src,
                this.buildPackage(procedure),
                this.encode(procedure),
                this.wrapReturnType(procedure));

        System.out.println(src);
        System.out.println(this.buildImports(procedure));
        System.out.println(buildParameterFields(procedure));

        src = Source.formatAligned(src, this.buildImports(procedure));
        src = Source.formatAligned(src, this.buildParameterFields(procedure));
        src = Source.formatAligned(src, this.buildConstructor(procedure));
        src = Source.formatAligned(src, this.buildReleaseArgs(procedure));

        return src;
    }

    private String buildPackage(Procedure procedure) {
        String pack = JavaModelInfo.getPackage(procedure.getReturnType());
        return pack.length() > 0 ? pack : PaniniModelInfo.DEFAULT_FUTURE_PACKAGE;
    }

    private String buildQualifiedClassName(Procedure procedure) {
        return this.buildPackage(procedure) + "." + this.encode(procedure);
    }

    private String buildClassName(Procedure procedure) {
        return this.encode(procedure);
    }

    private String encode(Procedure procedure) {
        return this.encodeReturnType(procedure) + "$Future$" + this.encodeParameters(procedure);
    }

    private List<String> buildImports(Procedure procedure) {
        TypeMirror mirror = procedure.getReturnType();
        TypeKind kind = mirror.getKind();

        List<String> packs = new ArrayList<String>();

        packs.add("java.util.concurrent.Future");
        packs.add("java.util.concurrent.ExecutionException");
        packs.add("java.util.concurrent.TimeUnit");
        packs.add("java.util.concurrent.TimeoutException");
        packs.add("org.paninij.runtime.Panini$Future");
        packs.add("org.paninij.runtime.Panini$Message");
        packs.add(wrapReturnType(procedure));

        switch (kind) {
        case ARRAY:
        case DECLARED:
            TypeElement typeElem = (TypeElement) ((DeclaredType) mirror).asElement();
            packs = Source.buildCollectedImportDecls(typeElem, packs);
            return packs;
        case BOOLEAN:
        case BYTE:
        case CHAR:
        case DOUBLE:
        case FLOAT:
        case INT:
        case LONG:
        case SHORT:
        case VOID:
            return Source.buildImportDecls(packs);
        default:
            String msg = "The given `return` (of the form `#0`) has an unexpected `TypeKind`: #1";
            msg = Source.format(msg, mirror, kind);
            throw new IllegalArgumentException(msg);
        }
    }

    private List<String> buildConstructor(Procedure procedure) {
        return this.buildConstructor(procedure, "");
    }

    private List<String> buildConstructor(Procedure procedure, String prependToBody) {
        // Create a list of parameters to the constructor starting with the `procID`.
        List<String> params = new ArrayList<String>();
        params.add("int procID");

        List<String> slots = this.getSlotTypes(procedure);
        int i = 0;
        for (String slot : slots) {
            params.add(slot + " arg" + (++i));
        }

        // Create a list of initialization statements.
        List<String> initializers = new ArrayList<String>();
        initializers.add("panini$procID = procID;");

        i = 0;
        for (String slot : slots) {
            initializers.add(Source.format("panini$arg#0 = arg#0;", ++i));
        }

        List<String> src = Source.lines("public #0(#1)",
                                        "{",
                                        "    #2",
                                        "    ##",
                                        "}");

        src = Source.formatAll(src, this.buildClassName(procedure),
                                    String.join(", ", params),
                                    prependToBody);
        src = Source.formatAlignedFirst(src, initializers);

        return src;
    }

    private List<String> buildReleaseArgs(Procedure procedure) {
        List<String> statements=  new ArrayList<String>();
        List<String> slots = this.getSlotTypes(procedure);
        int i = 0;
        for (String slot : slots) {
            if (slot.equals("java.lang.Object")) statements.add("panini$arg" + (++i) + " = null;");
        }
        return statements;
    }
}
