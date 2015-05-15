/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): David Johnston, Trey Erenberger
 */
package org.paninij.apt;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;

import org.paninij.apt.util.DuckShape;
import org.paninij.apt.util.JavaModelInfo;
import org.paninij.apt.util.Source;

public abstract class MakeDuck
{
    PaniniProcessor context;

    public static MakeDuck make(PaniniProcessor context)
    {
        throw new UnsupportedOperationException("Cannot instantiate an abstract class.");
    }

    String buildDuck(DuckShape currentDuck)
    {
        DuckShape.Category category = currentDuck.category;
        switch (category)
        {
        case NORMAL:
            return buildNormalDuck(currentDuck);
        case VOID:
            return buildVoidDuck(currentDuck);
        case PANINICUSTOM:
            return buildPaniniCustomDuck(currentDuck);
        default:
            throw new UnsupportedOperationException("Duck category not supported: " + category);
        }
    }

    // TODO: Consider replacing uses of `buildPackage()` with calls to `currentDuck.getPackage()`.
    String buildPackage(DuckShape currentDuck)
    {
        return currentDuck.getPackage();
    }

    abstract String buildClassName(DuckShape currentDuck);

    abstract String buildQualifiedClassName(DuckShape currentDuck);

    List<String> buildParameterFields(DuckShape currentDuck)
    {
        List<String> fields = new ArrayList<String>(currentDuck.slotTypes.size());
        for (int i = 0; i < currentDuck.slotTypes.size(); i++)
        {
            fields.add("public " + currentDuck.slotTypes.get(i) + " panini$arg" + i + ";");
        }
        return fields;
    }

    List<String> buildFacades(DuckShape currentDuck)
    {
        List<String> facades = new ArrayList<String>();

        DeclaredType returnType = (DeclaredType) currentDuck.returnType;
        for (Element el : returnType.asElement().getEnclosedElements())
        {
            if (el.getKind() == ElementKind.METHOD)
            {
                ExecutableElement method = (ExecutableElement) el;
                if (this.canMakeFacade(method))
                {
                    facades.addAll(buildFacade(method));
                    facades.add("");
                }
            }
        }

        return facades;
    }

    List<String> buildFacade(ExecutableElement method)
    {
        List<String> fmt = Source.lines("@Override",
                                        Source.buildExecutableDecl(method),
                                        "{",
                                        "    #0",
                                        "}");
        return Source.formatAll(fmt, buildFacadeBody(method));
    }

    String buildFacadeBody(ExecutableElement method)
    {
        String fmt;
        if (JavaModelInfo.hasVoidReturnType(method)) {
            fmt = "panini$get().#0(#1);";
        } else {
            fmt = "return panini$get().#0(#1);";
        }
        return Source.format(fmt, method.getSimpleName(), Source.buildParameterNamesList(method));
    }

    boolean canMakeFacade(ExecutableElement method)
    {
        // Some methods do not need to have a facade made for them
        // e.g. native methods, final methods
        String modifiers = Source.buildModifiersList(method);
        if (modifiers.contains("native"))
        {
            return false;
        }
        if (modifiers.contains("final"))
        {
            return false;
        }
        if (modifiers.contains("protected"))
        {
            return false;
        }
        if (modifiers.contains("private"))
        {
            return false;
        }
        if (modifiers.contains("private"))
        {
            return false;
        }
        if (modifiers.contains("static"))
        {
            return false;
        }
        return true;
    }

    List<String> buildReleaseArgs(DuckShape currentDuck) {

        List<String> statements = new ArrayList<String>();

        for(int i = 0; i < currentDuck.slotTypes.size(); i++)
        {
            if(currentDuck.slotTypes.get(i).equals("Object")) {
                statements.add("panini$arg" + i + " = null;");
            }
        }

        return statements;
    }

    abstract List<String> buildConstructor(DuckShape currentDuck);

    abstract String buildNormalDuck(DuckShape currentDuck);

    abstract String buildVoidDuck(DuckShape currentDuck);

    abstract String buildPaniniCustomDuck(DuckShape currentDuck);

    public abstract void makeSourceFile(DuckShape currentDuck);
}
