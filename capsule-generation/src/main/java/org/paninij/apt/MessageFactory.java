package org.paninij.apt;

import org.paninij.apt.util.MessageShape;
import org.paninij.apt.util.SourceFile;
import org.paninij.model.AnnotationKind;
import org.paninij.model.MessageKind;
import org.paninij.model.Procedure;
import org.paninij.apt.util.PaniniModelInfo;

import java.util.HashSet;

import javax.lang.model.type.TypeMirror;

public class MessageFactory
{
    private HashSet<String> generated;

    private FutureMessageSource futureSource;
    private SimpleMessageSource simpleSource;
    private DuckMessageSource duckSource;

    public MessageFactory() {
        this.generated = new HashSet<String>();
        this.futureSource = new FutureMessageSource();
        this.simpleSource = new SimpleMessageSource();
        this.duckSource = new DuckMessageSource();
    }

    public SourceFile make(Procedure procedure) {

        MessageShape shape = new MessageShape(procedure);

        if (this.generated.add(shape.encoded)) {
            switch (shape.category) {
            case DUCKFUTURE:
                return this.duckSource.generate(procedure);
            case FUTURE:
                return this.futureSource.generate(procedure);
            case PREMADE:
                // premade are already created
                return null;
            case SIMPLE:
                return this.simpleSource.generate(procedure);
            case ERROR:
            default:
                // TODO throw error here?
                System.out.println("Unhandled message kind");
                break;
            }
      }
        return null;
    }
}