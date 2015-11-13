package org.paninij.proc.check.template;

import static java.text.MessageFormat.format;

import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;

public class NoBadMethodNamesCheck extends AbstractTemplateCheck
{
    private static final String[] BAD_METHOD_NAMES = {
        "imports",
        "exit",
    };
    
    @Override
    protected Result checkTemplate(TemplateKind kind, TypeElement template) {
        for (Element elem : template.getEnclosedElements()) {
            if (isMethodWithBadName(elem)) {
                String err = "A {0} template cannot declare a method named `{1}()`.";
                err = format(err, kind, elem.getSimpleName());
                return new Error(err, NoBadMethodNamesCheck.class, elem);
            }
        }
        return ok;
    }
    
    private static boolean isMethodWithBadName(Element elem) {
        return elem.getKind() == ElementKind.METHOD
            && hasBadMethodName(elem);
    }
    
    private static boolean hasBadMethodName(Element elem) {
        String name = elem.getSimpleName().toString();
        for (String badName : BAD_METHOD_NAMES) {
            if (name.equals(badName)) {
                return true;
            }
        }
        return false;
    }
}
