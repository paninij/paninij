package org.paninij.apt;

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
      AnnotationKind annotation = procedure.getAnnotationKind();
      TypeMirror returnType = procedure.getReturnType();
      MessageKind messageKind = PaniniModelInfo.getMessageKind(returnType, annotation);

      SourceFile source = null;
      String encoded = null;

      // delegate to the correct class depending on message kind
      // also check if it was already created (in generated set)
      switch(messageKind) {
      case SIMPLE:
          encoded = this.simpleSource.encode(procedure);
          if (this.generated.add(encoded)) {
              source = this.simpleSource.generate(procedure);
              return source;
          }
          return null;
      case FUTURE:
          encoded = this.futureSource.encode(procedure);
          if (this.generated.add(encoded)) {
              source = this.futureSource.generate(procedure);
              return source;
          }
          return null;
      case DUCKFUTURE:
          encoded = this.duckSource.encode(procedure);
          if (this.generated.add(encoded)) {
              source = this.duckSource.generate(procedure);
              return source;
          }
          return null;
      case PREMADE:
      default:
          // TODO throw error here?
          System.out.println("Unhandled message kind");
          return null;
      }
    }
}
