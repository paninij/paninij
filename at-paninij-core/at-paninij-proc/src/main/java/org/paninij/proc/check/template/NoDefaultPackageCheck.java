package org.paninij.proc.check.template;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;

public class NoDefaultPackageCheck extends AbstractTemplateCheck {

	private boolean checkInDefault(TypeElement e) {
		return e.getQualifiedName().equals(e.getSimpleName());
	}
	
	@Override
	protected Result checkTemplate(TemplateKind templateKind, TypeElement template) {
		if (checkInDefault(template)) {
			return new Result.Error("Templates cannot be in the default pacakge.", NoDefaultPackageCheck.class, template);
		}
		return Result.ok;
	}

}
