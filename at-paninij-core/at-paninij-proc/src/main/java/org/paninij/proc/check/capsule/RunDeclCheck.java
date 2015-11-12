package org.paninij.proc.check.capsule;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Check;

public class RunDeclCheck extends DeclCheck
{
    @Override
    public Class<? extends Check> getErrorSource() {
        return RunDeclCheck.class;
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
