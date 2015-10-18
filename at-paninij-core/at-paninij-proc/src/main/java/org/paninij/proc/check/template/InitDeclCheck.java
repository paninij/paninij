package org.paninij.proc.check.template;

import javax.lang.model.element.ExecutableElement;

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
    public boolean hasValidParameters(ExecutableElement decl) {
        return decl.getParameters().isEmpty();
    }
}
