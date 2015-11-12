package org.paninij.proc.check.capsule;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Check;

public class InitDeclCheck extends DeclCheck
{
    @Override
    public Class<? extends Check> getErrorSource() {
        return InitDeclCheck.class;
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
