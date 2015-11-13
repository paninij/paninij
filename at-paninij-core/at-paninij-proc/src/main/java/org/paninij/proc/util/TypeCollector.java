
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
 * Use the type collector to gather a set of `String` representations of the fully qualified type
 * names being used on the interface of the visited type. For example, given some `Element elem`,
 *
 * ```
 * TypeCollector collector = new TypeCollector();
 * Set<String> collectedTypes = new HashSet<String>();
 * elem.accept(collector, collectedTypes);
 * ```
 *
 * One can use `TypeCollector.collect(Element e)` as a convenience method for doing this.
 *
 * Note that `visit(TypeMirror t)` should not be called; always use `visit(TypeMirror, Set<String>)`
 * instead. Doing otherwise will likely result in `NullPointerException`s being thrown.
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
