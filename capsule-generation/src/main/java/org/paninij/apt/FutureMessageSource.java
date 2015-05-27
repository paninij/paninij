package org.paninij.apt;

import java.util.ArrayList;
import java.util.List;

import org.paninij.apt.util.JavaModelInfo;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;
import org.paninij.apt.util.SourceFile;
import org.paninij.model.Procedure;

public class FutureMessageSource extends MessageSource
{
    public FutureMessageSource() {
        this.context = null;
    }

    /*
     * Create a new Source file (name and content)
     */
    @Override
    public SourceFile generate(Procedure procedure) {
        this.context = procedure;
        String name = this.buildQualifiedClassName();
        String content = this.generateContent();
        return new SourceFile(name, content);
    }

    @Override
    protected String generateContent() {
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
                "        //TODO throw error or implement timeout",
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
                this.buildPackage(),
                this.encode(),
                this.wrapReturnType());

        System.out.println(src);
        System.out.println(this.buildImports());
        System.out.println(this.buildParameterFields());

        src = Source.formatAligned(src, this.buildImports());
        src = Source.formatAligned(src, this.buildParameterFields());
        src = Source.formatAligned(src, this.buildConstructor());
        src = Source.formatAligned(src, this.buildReleaseArgs());

        return src;
    }

    @Override
    protected String encode() {
        return this.encodeReturnType() + "$Future$" + this.encodeParameters();
    }

    @Override
    protected List<String> buildImports() {
        List<String> packs = new ArrayList<String>();
        packs.add("java.util.concurrent.Future");
        packs.add("java.util.concurrent.ExecutionException");
        packs.add("java.util.concurrent.TimeUnit");
        packs.add("java.util.concurrent.TimeoutException");
        return super.buildImports(packs);
    }

    @Override
    protected String buildPackage() {
        String pack = JavaModelInfo.getPackage(this.context.getReturnType());
        return pack.length() > 0 ? pack : PaniniModelInfo.DEFAULT_FUTURE_PACKAGE;
    }

    @Override
    protected List<String> buildConstructor(String prependToBody) {
        // Create a list of parameters to the constructor starting with the `procID`.
        List<String> params = new ArrayList<String>();
        params.add("int procID");

        List<String> slots = this.getSlotTypes();
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

        src = Source.formatAll(src, this.encode(),
                                    String.join(", ", params),
                                    prependToBody);
        src = Source.formatAlignedFirst(src, initializers);

        return src;
    }

    protected List<String> buildReleaseArgs() {
        List<String> statements=  new ArrayList<String>();
        List<String> slots = this.getSlotTypes();
        int i = 0;
        for (String slot : slots) {
            if (slot.equals("java.lang.Object")) statements.add("panini$arg" + (++i) + " = null;");
        }
        return statements;
    }
}
