/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
package org.paninij.proc.check.capsule;

import static javax.lang.model.element.Modifier.*;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Checks that a core and its members do not have illegal modifiers. This does not do certain
 * checks related to specific core declarations (e.g. `init()`, whose checks are in
 * `CheckInitDecl`).
 */
public class CheckForIllegalModifiers implements CapsuleCheck
{
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
        PRIVATE,   // A capsule needs to be able to access fields of its encapsulated core.
        ABSTRACT,
        STATIC,
        TRANSIENT,
        VOLATILE,
    };

    public final static Modifier[] ILLEGAL_METHOD_MODIFIERS = {
        ABSTRACT,
        SYNCHRONIZED,
        NATIVE,
    };
    
    public final static Modifier[] ILLEGAL_RUN_DECL_MODIFIERS = {
        ABSTRACT,
        SYNCHRONIZED,
        NATIVE,
        STATIC,
        PRIVATE,
    };
    
    public final static Modifier[] ILLEGAL_INIT_DECL_MODIFIERS = {
        ABSTRACT,
        SYNCHRONIZED,
        NATIVE,
        STATIC,
        PRIVATE,
    };

    public final static Modifier[] ILLEGAL_DESIGN_DECL_MODIFIERS = {
        ABSTRACT,
        SYNCHRONIZED,
        NATIVE,
        STATIC,
        PRIVATE,
    };

    @Override
    public Result checkCapsule(TypeElement core)
    {
        Modifier illegalModifier = getIllegalModifier(core, ILLEGAL_TEMPLATE_MODIFIERS);
        if (illegalModifier != null) {
            String err = "A capsule core has an illegal modifier: " + illegalModifier;
            return error(err, CheckForIllegalModifiers.class, core);
        }
        
        for (Element member : core.getEnclosedElements())
        {
            Modifier[] illegalModifiers = lookupIllegalModifiers(member);
            if (illegalModifiers == EMPTY) {
                continue;
            }
            illegalModifier = getIllegalModifier(member, illegalModifiers);
            if (illegalModifier != null)
            {
                String err = "A capsule core member has an illegal modifier: " + illegalModifier;
                return error(err, CheckForIllegalModifiers.class, member);
            }
        }
        return OK;
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
            switch (member.getSimpleName().toString()) {
            case "init":
                return ILLEGAL_INIT_DECL_MODIFIERS;
            case "design":
                return ILLEGAL_DESIGN_DECL_MODIFIERS;
            case "run":
                return ILLEGAL_RUN_DECL_MODIFIERS;
            default:
                return ILLEGAL_METHOD_MODIFIERS;
            }
        default:
            return EMPTY;
        }
    }
}
