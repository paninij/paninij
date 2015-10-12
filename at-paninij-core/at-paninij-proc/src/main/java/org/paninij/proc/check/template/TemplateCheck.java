package org.paninij.proc.check.template;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;

public interface TemplateCheck
{
    Result check(TypeElement template);
}