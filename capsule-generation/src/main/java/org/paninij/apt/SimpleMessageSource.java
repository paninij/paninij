package org.paninij.apt;

import java.util.List;

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
        String name = this.buildQualifiedClassName();
        String content = this.generateContent();
        return new SourceFile(name, content);
    }

    @Override
    protected String generateContent() {
        return "";
    }

    @Override
    protected String encode() {
        return this.encodeReturnType() + "$Simple$" + this.encodeParameters();
    }

    @Override
    protected String buildPackage() {
        return "";
    }

    @Override
    protected List<String> buildConstructor(String prependToBody) {
        // TODO Auto-generated method stub
        return null;
    }
}
