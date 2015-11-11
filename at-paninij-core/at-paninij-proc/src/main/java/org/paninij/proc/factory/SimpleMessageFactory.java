/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills
 */
package org.paninij.proc.factory;

import org.paninij.proc.PaniniProcessor;
import org.paninij.proc.model.Procedure;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.Source;
import org.paninij.proc.util.SourceFile;

public class SimpleMessageFactory extends AbstractMessageFactory
{
    public SimpleMessageFactory() {
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
                "import org.paninij.runtime.Panini$Message;",
                "import javax.annotation.Generated;",
                "",
                "#1",
                "public class #2 implements Panini$Message",
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

        src = Source.format(src, this.shape.getPackage(),
                                 PaniniProcessor.getGeneratedAnno(SimpleMessageFactory.class),
                                 this.shape.encoded);
        src = Source.formatAligned(src, this.buildParameterFields());
        src = Source.formatAligned(src, this.buildConstructor());
        return src;
    }
}
