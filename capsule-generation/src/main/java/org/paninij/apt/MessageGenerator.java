package org.paninij.apt;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.paninij.apt.util.DuckShape;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;
import org.paninij.apt.util.DuckShape.Category;
import org.paninij.model.AnnotationKind;
import org.paninij.model.Capsule;
import org.paninij.model.MessageKind;
import org.paninij.model.Procedure;
import org.paninij.model.Variable;

public class MessageGenerator
{
    private PaniniProcessor context;

    public static void generate(PaniniProcessor context, Capsule capsule) {
        MessageGenerator generator = new MessageGenerator();
        generator.context = context;
        generator.generateMessages(capsule);
    }

    private void generateMessages(Capsule capsule) {
        for (Procedure procedure : capsule.getProcedures()) {
            this.generateMessage(procedure);
        }
    }

    private void generateMessage(Procedure procedure) {
        AnnotationKind annotation = procedure.getAnnotationKind();
        TypeMirror returnType = procedure.getReturnType();
        MessageKind messageKind = PaniniModelInfo.getMessageKind(returnType, annotation);
        switch(messageKind) {
        case SIMPLE:
            SimpleMessageGenerator.generate(context, procedure);
            break;
        case FUTURE:
            FutureMessageGenerator.generate(context, procedure);
            break;
        case DUCKFUTURE:
            DuckMessageGenerator.generate(context, procedure);
            break;
        case PREMADE:
        default:
            System.out.println("Unhandled message kind");
        }
    }

    protected String encodeReturnType(Procedure procedure) {
        TypeMirror returnType = procedure.getReturnType();
        return returnType.toString().replaceAll("_", "__").replaceAll("\\.", "_");
    }

    protected String encodeParameters(Procedure procedure) {
        List<String> slots = getSlotTypes(procedure);
        return String.join("$", slots);
    }

    protected List<String> getSlotTypes(Procedure procedure) {
        List<Variable> args = procedure.getParameters();
        List<String> slots = new ArrayList<String>();
        for (Variable arg : args) {
            slots.add(getSlotType(arg));
        }
        return slots;
    }

    protected String getSlotType(Variable variable) {
        // TODO: Look for another way to get the strings in the primitive cases.
        TypeKind kind = variable.getType().getKind();

        switch (kind) {
        case ARRAY:
        case DECLARED:
            return "ref";
        case BOOLEAN:
            return "boolean";
        case BYTE:
            return "byte";
        case CHAR:
            return "char";
        case DOUBLE:
            return "double";
        case FLOAT:
            return "float";
        case INT:
            return "int";
        case LONG:
            return "long";
        case SHORT:
            return "short";
        default:
            String msg = "The given `param` (of the form `#0`) has an unexpected `TypeKind`: #1";
            msg = Source.format(msg, variable, kind);
            throw new IllegalArgumentException(msg);
        }
    }

    protected String wrapReturnType(Procedure procedure) {
        TypeMirror mirror = procedure.getReturnType();
        TypeKind kind = mirror.getKind();

        switch (kind) {
        case ARRAY:
        case DECLARED:
            return mirror.toString();
        case BOOLEAN:
            return "java.lang.Boolean";
        case BYTE:
            return "java.lang.Byte";
        case CHAR:
            return "java.lang.Char";
        case DOUBLE:
            return "java.lang.Double";
        case FLOAT:
            return "java.lang.Float";
        case INT:
            return "java.lang.Integer";
        case LONG:
            return "java.lang.Long";
        case SHORT:
            return "java.lang.Short";
        case VOID:
            return "java.lang.Void";
        default:
            String msg = "The given `return` (of the form `#0`) has an unexpected `TypeKind`: #1";
            msg = Source.format(msg, mirror, kind);
            throw new IllegalArgumentException(msg);
        }
    }

    protected List<String> buildParameterFields(Procedure procedure)
    {
        List<String> slots = this.getSlotTypes(procedure);
        List<String> fields = new ArrayList<String>(slots.size());
        int i = 0;
        for (String slot : slots) {
            fields.add("public " + slot + " panini$arg" + (++i) + ";");
        }
        return fields;
    }

}
