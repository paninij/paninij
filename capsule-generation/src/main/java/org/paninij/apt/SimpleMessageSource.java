package org.paninij.apt;

import org.paninij.apt.util.MessageShape;
import org.paninij.apt.util.Source;
import org.paninij.apt.util.SourceFile;
import org.paninij.model.Procedure;

public class SimpleMessageSource extends MessageSource
{

    public SimpleMessageSource() {
        this.context = null;
    }

    /*
     * Create a new Source file (name and content)
     */
    @Override
    public SourceFile generate(Procedure procedure) {
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
                "import org.paninij.runtime.Panini$Message;",
                "",
                "public class #1 implements Panini$Message",
                "{",
                "    public final int panini$procID;",
                "",
                "    ##",
                "",
                "    ##",
                "",
                "    @Override",
                "    public int panini$msgID() {",
                "        return panini$procID;",
                "    }",
                "}");

        src = Source.format(src, this.shape.getPackage(), this.shape.encoded);
        src = Source.formatAligned(src, this.buildParameterFields());
        src = Source.formatAligned(src, this.buildConstructor());
        return src;
    }
}
