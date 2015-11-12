package org.paninij.proc.check.capsule;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.proc.check.Check;

public class DesignDeclCheck extends DeclCheck
{
    @Override
    public Class<? extends Check> getErrorSource() {
        return DesignDeclCheck.class;
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

        // Note that `self` is usually `NONE` type kind, which indicates that a capsule interface
        // artifact has yet not been generated. However, if there is already a capsule interface
        // type around, then we will have fully qualified type information. This is why we compare
        // the `actual` with the suffix of the `expected`.
        TypeMirror self = params.get(0).asType();
        String actual = self.toString() + "Template";
        String expected = template.getQualifiedName().toString();

        return expected.endsWith(actual);
    }

}
