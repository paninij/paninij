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

package org.paninij.proc.util;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;


/**
 * Use the type collector to gather a set of {@link String} representations of the fully qualified
 * type names being used on the interface of the visited type. For example, given some
 * {@code Element elem},
 *
 * <pre><code>
 *   TypeCollector collector = new TypeCollector();
 *   Set&lt;String&gt; collectedTypes = new HashSet&lt;String&gt;();
 *   elem.accept(collector, collectedTypes);
 * </code></pre>
 *
 * <p>One can use {@code TypeCollector.collect(Element e)} as a convenience method for doing this.
 *
 * <p>Note that {@code visit(TypeMirror t)} should not be called; always use {@code
 * visit(TypeMirror, Set&lt;String&gt;)} instead. Doing otherwise will likely result in a {@code
 * NullPointerException} being thrown.
 */
public class TypeCollector extends SimpleElementVisitor8<Void, Set<String>>
{
    public static Set<String> collect(Element e)
    {
        TypeCollector collector = new TypeCollector();
        Set<String> collected = new HashSet<String>();
        e.accept(collector, collected);
        return collected;
    }

    @Override
    public Void visitExecutable(ExecutableElement e, Set<String> p)
    {
        p.addAll(Helper.collect(e.getReturnType()));
        for (VariableElement var : e.getParameters()) {
            var.accept(this, p);
        }
        for (TypeMirror thrown : e.getThrownTypes()) {
            p.addAll(Helper.collect(thrown));
        }

        return null;
    }

    @Override
    public Void visitType(TypeElement e, Set<String> p)
    {
        p.addAll(Helper.collect(e.getSuperclass()));

        for (TypeMirror i : e.getInterfaces()) {
            p.addAll(Helper.collect(i));
        }

        for (Element enclosedElem : e.getEnclosedElements()) {
            enclosedElem.accept(this, p);
        }

        return null;
    }

    @Override
    public Void visitVariable(VariableElement e, Set<String> p)
    {
        p.addAll(Helper.collect(e.asType()));
        return null;
    }


    /**
     * A `TypeMirror` visitor to be used to collect the types used in the definition of a type (as
     * represented by a `TypeMirror`).
     */
    private static class Helper extends SimpleTypeVisitor8<Void, Set<String>>
    {
        public static Set<String> collect(TypeMirror t)
        {
            Helper collector = new Helper();
            Set<String> collected = new HashSet<String>();
            t.accept(collector, collected);
            return collected;
        }

        @Override
        public Void visitArray(ArrayType t, Set<String> p)
        {
            t.getComponentType().accept(this, p);
            return null;
        }

        @Override
        public Void visitDeclared(DeclaredType t, Set<String> p)
        {
            p.add(Source.buildWithoutTypeArgs(t));
            for (TypeMirror typeArg : t.getTypeArguments()) {
                typeArg.accept(this, p);
            }
            return null;
        }
    }
}
