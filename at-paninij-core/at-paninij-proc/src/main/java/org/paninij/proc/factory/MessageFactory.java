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

import org.paninij.proc.model.Procedure;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.SourceFile;

import java.util.HashSet;


public class MessageFactory implements ArtifactFactory<Procedure>
{
    private HashSet<String> generated;

    private FutureMessageFactory futureMessageFactory;
    private SimpleMessageFactory simpleMessageFactory;
    private DuckMessageFactory duckMessageFactory;

    public MessageFactory() {
        this.generated = new HashSet<String>();
        this.futureMessageFactory = new FutureMessageFactory();
        this.simpleMessageFactory = new SimpleMessageFactory();
        this.duckMessageFactory = new DuckMessageFactory();
    }

    public SourceFile make(Procedure procedure) {

        MessageShape shape = new MessageShape(procedure);

        if (this.generated.add(shape.encoded))
        {
            switch (shape.category) {
            case DUCKFUTURE:
                return this.duckMessageFactory.make(procedure);
            case FUTURE:
                return this.futureMessageFactory.make(procedure);
            case PREMADE:
                // premade are already created
                return null;
            case SIMPLE:
                return this.simpleMessageFactory.make(procedure);
            case ERROR:
            default:
                String err = "Procedure has unknown message kind: " + shape.category;
                throw new IllegalArgumentException(err);
            }
        }
        return null;
    }
}
