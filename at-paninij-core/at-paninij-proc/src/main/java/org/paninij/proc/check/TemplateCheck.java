package org.paninij.proc.check;

import javax.lang.model.element.TypeElement;

public interface TemplateCheck
{
    Result check(TypeElement template);
}