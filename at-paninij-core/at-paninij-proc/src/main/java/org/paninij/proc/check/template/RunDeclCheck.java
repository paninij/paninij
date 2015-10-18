package org.paninij.proc.check.template;

import javax.lang.model.element.ExecutableElement;

public class RunDeclCheck extends DeclCheck
{
    private final static String ERROR_SOURCE = RunDeclCheck.class.toString();
    
    @Override
    public String getErrorSource() {
        return ERROR_SOURCE;
    }

    @Override
    public String getDeclName() {
        return "run";
    }

    @Override
    public boolean hasValidParameters(ExecutableElement decl) {
        return decl.getParameters().isEmpty();
    }
}
