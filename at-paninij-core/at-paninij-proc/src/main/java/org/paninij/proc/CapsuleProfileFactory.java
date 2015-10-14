package org.paninij.proc;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.TypeKind;

import org.paninij.proc.model.Procedure;
import org.paninij.proc.model.Variable;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.PaniniModel;
import org.paninij.proc.util.Source;


public abstract class CapsuleProfileFactory extends CapsuleArtifactFactory
{
    protected abstract String generateClassName();

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
        String encoding = PaniniModel.isPaniniCustom(shape.returnType.getMirror()) ? shape.returnType.raw() : shape.encoded;
        
        List<String> source = Source.lines(
                "@Override",
                "#0",
                "{",
                "    #1 panini$message = null;",
                "    panini$message = new #1(#2);",
                "    panini$push(panini$message);",
                "    #3",
                "}",
                "");
        
        return Source.formatAll(source,
                this.generateProcedureDecl(shape),
                encoding,
                this.generateProcedureArguments(shape),
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

    protected List<String> generateCheckRequiredFields()
    {
        // Get the fields which must be non-null, i.e. all @Import fields and all arrays of locals.
        List<Variable> required = this.capsule.getImportFields();

        for (Variable local : this.capsule.getLocalFields()) {
            if (local.isArray()) required.add(local);
        }

        if (required.isEmpty()) return new ArrayList<String>();

        List<String> assertions = new ArrayList<String>(required.size());
        for (int idx = 0; idx < required.size(); idx++) {
            if (required.get(idx).isCapsule()) {
                assertions.add(Source.format(
                        "assert(panini$encapsulated.#0 != null);",
                        required.get(idx).getIdentifier()));
            }
        }

        List<String> lines = Source.lines(
                "@Override",
                "public void panini$checkRequiredFields() {",
                "    ##",
                "}",
                "");
        return Source.formatAlignedFirst(lines, assertions);
    }

    protected List<String> generateExport()
    {
        List<Variable> imported = this.capsule.getImportFields();
        List<String> refs = new ArrayList<String>();
        List<String> decls = new ArrayList<String>();

        if (imported.isEmpty()) return refs;

        for (Variable var : imported) {
            String instantiation = Source.format("panini$encapsulated.#0 = #0;", var.getIdentifier());
            refs.add(instantiation);

            if (var.isArray()) {
                if (var.getEncapsulatedType().isCapsule()) {
                    List<String> lines = Source.lines(
                            "for (int i = 0; i < panini$encapsulated.#0.length; i++) {",
                            "    ((Panini$Capsule) panini$encapsulated.#0[i]).panini$openLink();",
                            "}");
                    refs.addAll(Source.formatAll(
                            lines,
                            var.getIdentifier()));
                }
            } else {
                if (var.isCapsule()) {
                    refs.add(Source.format("((Panini$Capsule) panini$encapsulated.#0).panini$openLink();", var.getIdentifier()));
                }
            }

            decls.add(var.toString());
        }

        List<String> src = Source.lines(
                "public void imports(#0) {",
                "    ##",
                "}",
                "");

        src = Source.formatAll(src, String.join(", ", decls));
        src = Source.formatAlignedFirst(src, refs);

        return src;
    }

    protected List<String> generateGetAllState()
    {
    	List<String> states = new ArrayList<String>();
    	
    	for(Variable field : capsule.getStateFields())
    	{
    		if(field.getKind() == TypeKind.ARRAY || field.getKind() == TypeKind.DECLARED)
    		{
    			states.add("panini$encapsulated." + field.getIdentifier());
    		}
    	}
    	
        List<String> src = Source.lines("@Override",
                                        "public Object panini$getAllState()",
                                        "{",
                                        "    Object[] state = {#0};",
                                        "    return state;",
                                        "}",
                                        "");

        return Source.formatAll(src, String.join(", ", states));
    }

    protected List<String> generateInitState()
    {
        if (!this.capsule.hasInit()) return new ArrayList<String>();
        return Source.lines(
                "@Override",
                "protected void panini$initState() {",
                "    panini$encapsulated.init();",
                "}",
                "");
    }

    protected List<String> generateOnTerminate() {
        List<String> shutdowns = new ArrayList<String>();
        List<Variable> references = new ArrayList<Variable>();

        references.addAll(this.capsule.getImportFields());
        references.addAll(this.capsule.getLocalFields());

        if (references.isEmpty()) return shutdowns;

        for (Variable reference : references) {
            if (reference.isArray()) {
                if (reference.getEncapsulatedType().isCapsule()) {
                    List<String> src = Source.lines(
                            "for (int i = 0; i < panini$encapsulated.#0.length; i++) {",
                            "    ((Panini$Capsule) panini$encapsulated.#0[i]).panini$closeLink();",
                            "}");
                    shutdowns.addAll(Source.formatAll(src, reference.getIdentifier()));
                }
            } else {
                if (reference.isCapsule()) {
                    shutdowns.add(Source.format("((Panini$Capsule) panini$encapsulated.#0).panini$closeLink();", reference.getIdentifier()));
                }
            }
        }

        List<String> src = Source.lines(
                "@Override",
                "protected void panini$onTerminate() {",
                "    ##",
                "    this.panini$terminated = true;",
                "}",
                "");

        return Source.formatAlignedFirst(src, shutdowns);
    }

    protected boolean deservesMain()
    {
        // if the capsule has external dependencies, it does
        // not deserve a main
        if (!this.capsule.getImportFields().isEmpty()) return false;

        if (this.capsule.isActive()) {
            // if the capsule is active and has no external deps,
            // it deserves a main
            return true;
        } else {
            // if the capsule has no locals, it does not need a main
            // (this is a bogus/dull scenario)
            if (this.capsule.getLocalFields().isEmpty()) return false;

            // check if any ancestor capsules are active
            if (this.capsule.hasActiveAncestor()) return true;

            // if no local is active, this does not deserve a main
            return false;
        }
    }

    protected List<String> generateMain()
    {
        if (!this.deservesMain()) return new ArrayList<String>();

        List<String> src = Source.lines(
                "public static void main(String[] args) {",
                "    try {",
                "        Panini$System.threads.countUp();",
                "        #0 root = new #0();",
                "        root.run();",
                "    } catch (InterruptedException e) {",
                "       e.printStackTrace();",
                "    }",
                "}");

        return Source.formatAll(src, this.generateClassName());
    }
}
