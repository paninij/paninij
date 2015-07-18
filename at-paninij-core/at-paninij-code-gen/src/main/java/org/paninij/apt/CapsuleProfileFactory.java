package org.paninij.apt;

import java.util.ArrayList;
import java.util.List;

import org.paninij.apt.model.Procedure;
import org.paninij.apt.model.Variable;
import org.paninij.apt.util.MessageShape;
import org.paninij.apt.util.Source;


public abstract class CapsuleProfileFactory extends CapsuleArtifactFactory
{
    protected String generateProcedureID(Procedure p) {
        String base = "panini$proc$";
        List<String> params = new ArrayList<String>();

        for (Variable param : p.getParameters()) {
            params.add(param.encodeFull());
        }

        String paramStrings = params.size() > 0 ? "$" + String.join("$", params) : "";

        return base + p.getName() + paramStrings;
    }

    protected String generateProcedureReturn(MessageShape shape) {
        switch (shape.behavior) {
        case BLOCKED_FUTURE:
            String ret = shape.returnType.isVoid() ? "" : "return ";
            ret += "panini$message.get();";
            return ret;
        case ERROR:
            break;
        case BLOCKED_PREMADE:
            return "return panini$message.get();";
        case UNBLOCKED_DUCK:
        case UNBLOCKED_FUTURE:
        case UNBLOCKED_PREMADE:
            return "return panini$message;";
        case UNBLOCKED_SIMPLE:
            return "";
        default:
            break;
        }

        return null;
    }

    protected String generateProcedureArguments(MessageShape shape) {
        String procID = this.generateProcedureID(shape.procedure);
        List<String> argNames = new ArrayList<String>();
        argNames.add(procID);
        argNames.addAll(this.generateProcArgumentNames(shape.procedure));
        return String.join(", ", argNames);
    }

    protected List<String> generateProcedure(Procedure procedure) {
        MessageShape shape = new MessageShape(procedure);

        List<String> source = Source.lines(
                "@Override",
                "#0",
                "{",
                "    #1 panini$message = null;",
                "    panini$message = new #1(#2);",
                "    #3;",
                "    panini$push(panini$message);",
                "    #4",
                "}",
                "");
        return Source.formatAll(source,
                this.generateProcedureDecl(shape),
                shape.encoded,
                this.generateProcedureArguments(shape),
                this.generateAssertSafeInvocationTransfer(),
                this.generateProcedureReturn(shape));
    }

    protected List<String> generateProcArgumentDecls(Procedure p) {
        List<String> argDecls = new ArrayList<String>();
        for (Variable v : p.getParameters()) {
            argDecls.add(v.toString());
        }
        return argDecls;
    }

    protected List<String> generateProcArgumentNames(Procedure p) {
        List<String> argNames = new ArrayList<String>();
        for (Variable v : p.getParameters()) {
            argNames.add(v.getIdentifier());
        }
        return argNames;
    }

    protected String generateProcedureDecl(MessageShape shape) {
        List<String> argDecls = this.generateProcArgumentDecls(shape.procedure);
        String argDeclString = String.join(", ", argDecls);
        String declaration = Source.format("public #0 #1(#2)",
                shape.realReturn,
                shape.procedure.getName(),
                argDeclString);
        List<String> thrown = shape.procedure.getThrown();
        declaration += (thrown.isEmpty()) ? "" : " throws " + String.join(", ", thrown);
        return declaration;
    }

    String generateAssertSafeInvocationTransfer()
    {
        return Source.format("assert DynamicOwnershipTransfer.#0.isSafeTransfer(#1, #2): #3",
                             PaniniProcessor.dynamicOwnershipTransferKind,
                             "panini$message",
                             "Panini$System.self.get().panini$getAllState()",
                             "\"Procedure invocation performed unsafe ownership transfer.\"");
    }
}
