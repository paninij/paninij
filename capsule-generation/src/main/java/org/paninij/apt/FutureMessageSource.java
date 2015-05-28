package org.paninij.apt;

import java.util.ArrayList;
import java.util.List;

import org.paninij.apt.util.JavaModelInfo;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;
import org.paninij.apt.util.SourceFile;
import org.paninij.model.Procedure;
import org.paninij.model.Type.Category;
import org.paninij.model.Variable;

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
                this.shape.encoded,
                this.context.getReturnType().wrapped());

        src = Source.formatAligned(src, this.buildImports());
        src = Source.formatAligned(src, this.buildParameterFields());
        src = Source.formatAligned(src, this.buildConstructor());
        src = Source.formatAligned(src, this.buildReleaseArgs());

        return src;
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
        String pack = JavaModelInfo.getPackage(this.context.getReturnType().getMirror());
        return pack.length() > 0 ? pack : PaniniModelInfo.DEFAULT_FUTURE_PACKAGE;
    }

    protected List<String> buildReleaseArgs() {
        List<String> statements=  new ArrayList<String>();
        int i = 0;
        for (Variable v : context.getParameters()) {
            if (v.getCategory() == Category.NORMAL) statements.add("panini$arg" + (++i) + " = null;");
        }
        return statements;
    }
}
