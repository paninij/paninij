package org.paninij.proc.check.template;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class InitDeclCheck extends DeclCheck
{
    private final static String ERROR_SOURCE = InitDeclCheck.class.toString();
    
    @Override
    public String getErrorSource() {
        return ERROR_SOURCE;
    }

    @Override
    public String getDeclName() {
        return "init";
    }

    @Override
    public boolean hasValidParameters(TypeElement template, ExecutableElement decl) {
        return decl.getParameters().isEmpty();
    }
}
