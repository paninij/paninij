
package org.paninij.proc.factory;

import java.util.ArrayList;
import java.util.List;

import org.paninij.proc.PaniniProcessor;
import org.paninij.proc.model.Procedure;
import org.paninij.proc.model.Variable;
import org.paninij.proc.model.Type.Category;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.Source;
import org.paninij.proc.util.SourceFile;

public class FutureMessageFactory extends AbstractMessageFactory
{
    public FutureMessageFactory() {
        this.context = null;
    }

    /*
     * Create a new Source file (name and content)
     */
    @Override
    public SourceFile make(Procedure procedure) {
        this.context = procedure;
        this.shape = new MessageShape(procedure);
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
                "#1",
                "@SuppressWarnings(\"all\")",  // Suppress unused imports.
                "public class #2 implements Panini$Message, Panini$Future<#3>, Future<#3>", //TODO drop the panini$future
                "{",
                "    public final int panini$procID;",
                "    private #3 panini$result = null;",
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
                "    public void panini$resolve(#3 result) {",
                "        synchronized (this) {",
                "            panini$result = result;",
                "            panini$isResolved = true;",
                "            this.notifyAll();",
                "        }",
                "        ##",
                "    }",
                "",
                "    @Override",
                "    public #3 panini$get() {",
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
                "    public #3 get() {",
                "        return this.panini$get();",
                "    }",
                "",
                "    @Override",
                "    public #3 get(long timeout, TimeUnit unit)",
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
                this.shape.getPackage(),
                PaniniProcessor.getGeneratedAnno(FutureMessageFactory.class),
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
        packs.add("javax.annotation.Generated");
        packs.add("java.util.concurrent.Future");
        packs.add("java.util.concurrent.ExecutionException");
        packs.add("java.util.concurrent.TimeUnit");
        packs.add("java.util.concurrent.TimeoutException");
        return super.buildImports(packs);
    }

    protected List<String> buildReleaseArgs() {
        List<String> statements =  new ArrayList<String>();
        int i = 0;
        for (Variable v : context.getParameters()) {
            if (v.getCategory() == Category.NORMAL) statements.add("panini$arg" + (i++) + " = null;");
        }
        return statements;
    }
}
