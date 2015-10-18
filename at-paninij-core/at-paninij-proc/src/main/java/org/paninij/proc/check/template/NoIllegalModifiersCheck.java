package org.paninij.proc.check.template;

import static java.text.MessageFormat.format;

import static javax.lang.model.element.Modifier.*;

import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;

/**
 * Checks that a template and its members do not have illegal modifiers. This does not do certain
 * checks related to specific template declarations (e.g. `init()`, whose checks are in
 * `InitDeclCheck`).
 */
public class NoIllegalModifiersCheck implements TemplateCheck
{
    public final static String ERROR_SOURCE = NoIllegalModifiersCheck.class.getName();
    
    private final static Modifier[] EMPTY = { };
    
    public final static Modifier[] ILLEGAL_TEMPLATE_MODIFIERS = {
        ABSTRACT,
    };
    
    public final static Modifier[] ILLEGAL_CONSTRUCTOR_MODIFIERS = {
        PRIVATE,
    };

    public final static Modifier[] ILLEGAL_FIELD_MODIFIERS = {
        PUBLIC,
        PROTECTED,
        ABSTRACT,
        STATIC,
        TRANSIENT,
        VOLATILE,
    };

    public final static Modifier[] ILLEGAL_METHOD_MODIFIERS = {
        ABSTRACT,
        DEFAULT,
        SYNCHRONIZED,
        NATIVE,
    };
    
    @Override
    public Result check(TypeElement template)
    {
        Modifier illegalModifier = getIllegalModifier(template, ILLEGAL_TEMPLATE_MODIFIERS);
        if (illegalModifier != null) {
            String err = "Capsule template `{0}` has an illegal modifier: `{1}`.";
            err = format(err, template.getQualifiedName(), illegalModifier);
            return new Error(err, ERROR_SOURCE);
        }
        
        for (Element member : template.getEnclosedElements())
        {
            Modifier[] illegalModifiers = lookupIllegalModifiers(member);
            if (illegalModifiers == EMPTY) {
                continue;
            }
            illegalModifier = getIllegalModifier(member, illegalModifiers);
            if (illegalModifier != null)
            {
                String err = "A member of a capsule template, `{0}`, has an illegal modifier: "
                           + "a {1} includes the `{2}` modifier.";
                err = format(err, template.getQualifiedName(), member.getKind(), illegalModifier);
                return new Error(err, ERROR_SOURCE);
            }
        }
        return ok;
    }
    
    /**
     * @return  If there are illegal modifiers on `elem` (according to the given array), then one
     *          such modifier is returned. Otherwise, in the case that all modifiers on `elem` are
     *          considered legal, `null` is returned.
     */
    private static Modifier getIllegalModifier(Element elem, Modifier[] illegalModifiers)
    {
        for (Modifier modifier : elem.getModifiers()) {
            for (Modifier illegal : illegalModifiers) {
                if (modifier == illegal) {
                    return modifier;
                }
            }
        }
        return null;
    }

    /**
     * @return  An array of modifiers which are illegal for the given member to have. If there are
     *          no modifiers which are illegal for some member, then `EMPTY` is returned.
     */
    private static Modifier[] lookupIllegalModifiers(Element member)
    {
        switch (member.getKind()) {
        case CONSTRUCTOR:
            return ILLEGAL_CONSTRUCTOR_MODIFIERS;
        case FIELD:
            return ILLEGAL_FIELD_MODIFIERS;
        case METHOD:
            return ILLEGAL_METHOD_MODIFIERS;
        default:
            return EMPTY;
        }
    }
}