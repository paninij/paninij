package org.paninij.proc.check.capsule;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

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
    public boolean hasValidParameters(TypeElement template, ExecutableElement decl) {
        return decl.getParameters().isEmpty();
    }
}
