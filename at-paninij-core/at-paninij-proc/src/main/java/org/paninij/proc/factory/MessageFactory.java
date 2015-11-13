
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
