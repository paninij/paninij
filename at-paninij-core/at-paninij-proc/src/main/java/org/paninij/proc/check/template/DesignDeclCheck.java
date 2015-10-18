package org.paninij.proc.check.template;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class DesignDeclCheck extends DeclCheck
{
    private final static String ERROR_SOURCE = DesignDeclCheck.class.toString();

    @Override
    public String getErrorSource() {
        return ERROR_SOURCE;
    }

    @Override
    public String getDeclName() {
        return "design";
    }

    @Override
    public boolean hasValidParameters(TypeElement template, ExecutableElement decl)
    {
        List<? extends VariableElement> params = decl.getParameters();
        if (params.size() != 1) {
            return false;
        }

        // TODO: Fix this hack.
        VariableElement self = params.get(0);
        String actual = self.asType().toString() + "Template";
        String expected = template.getSimpleName().toString();
        if (!actual.equals(expected)) {
            return false;
        }

        return true;
    }

}
