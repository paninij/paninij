package org.paninij.apt.util;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

/**
 * This provides functionality similar to the most basic usage of
 * `MessageFormat`, but with some features that make it more simpler to use when
 * constructing source code.
 */
public class Source
{
    public static String lines(int depth, String... lines)
    {
        String tabs = "";
        for (int i = 0; i < depth; i++)
        {
            tabs += "    ";
        }

        String[] tabbed = new String[lines.length];
        for (int i = 0; i < lines.length; i++)
        {
            tabbed[i] = tabs + lines[i];
        }

        return String.join("\n", tabbed) + "\n";
    }

    /**
     * State labels used while parsing a format string and constructing the
     * result.
     */
    private enum FormatState
    {
        LITERAL,  // The last-seen character was a string-literal (or the scan just started).
        HASH,     // The last-seen character was '#'.
        HASH_NUM, // The last-seen character was part of a '#'-number identifier.
    }

    /**
     * Inserts any elements in the given list of items into the format string at
     * format elements. A format element is a substring of `fmt` that satisfies
     * the pattern "#\d+", that is, a hash- symbol followed by one or more
     * digits. The digits of a format element are interpreted to be the index
     * into `items` used to find which item to place at that location. For
     * example,
     *
     * Source.format("#0? #1, #0...", "World", "Hello") -> "World? Hello, World..."
     */
    public static String format(String fmt, Object... items)
    {
        // Make its initial capacity comparable to the length of the given
        // `fmt`.
        StringBuilder result = new StringBuilder(fmt.length());

        // A temporary to hold format element indices (without '#'-prefix) as
        // `fmt` is scanned.
        String idxStr = "";

        FormatState state = FormatState.LITERAL;
        for (char c : fmt.toCharArray())
        {
            switch (state)
            {

            case LITERAL:
                if (c == '#')
                {
                    state = FormatState.HASH;
                }
                else
                {
                    result.append(c);
                }
                continue;

            case HASH:
                if (Character.isDigit(c))
                {
                    // Interpret it as a format element.
                    idxStr = Character.toString(c);
                    state = FormatState.HASH_NUM;
                }
                else
                {
                    // Don't interpret it as a format element.
                    result.append('#');
                    result.append(c);
                    state = FormatState.LITERAL;
                }
                continue;

            case HASH_NUM:
                if (Character.isDigit(c))
                {
                    // The format element continues.
                    idxStr += c;
                }
                else
                {
                    // A format element has been fully parsed.
                    int idx = Integer.parseInt(idxStr);
                    if (idx >= items.length)
                    {
                        String msg = "The format element's index, " + idxStr + ", is too large.";
                        throw new IllegalArgumentException(msg);
                    }
                    result.append(items[idx]);
                    result.append(c);
                    state = FormatState.LITERAL;
                }
                continue;
            }
        }

        return result.toString();
    }

    public static String dropPackageName(String qualifiedClassName)
    {
        return qualifiedClassName.substring(qualifiedClassName.lastIndexOf('.') + 1);
    }
    
    /**
     * Builds a `String` matching the declaration of the executable element. For example, if the
     * given `exec` has the form
     * 
     *     public static void foo(int i, String str)
     * 
     * then this method would return "public static void foo(int i, String str)".
     */
    public static String buildExecutableDecl(ExecutableElement exec)
    {
        return format("#0 #1 #2(#3)", buildModifiersList(exec),
                                      exec.getReturnType(),
                                      exec.getSimpleName(),
                                      buildParametersList(exec));
    }

    /**
     * Builds a `String` matching the declared modifiers of the executable element. For example, if
     * the given `exec` has the form
     * 
     *     public static void foo(int i, String str)
     * 
     * then this method will return "public static".
     * 
     * If the given `exec` has no parameters, then the empty string is returned.
     */
    public static String buildModifiersList(ExecutableElement exec)
    {
        List<String> modifiers = new ArrayList<String>();
        for (Modifier m : exec.getModifiers())
        {
            modifiers.add(m.toString());
        }
        return String.join(" ", modifiers);
    }

    /**
     * Builds a `String` matching the parameter list of the given executable element. For example,
     * if the given `exec` has the form
     * 
     *     public static void foo(int i, String str)
     * 
     * then this method would return "int i, String str".
     * 
     * If the given `exec` has no parameters, then the empty string is returned.
     */
    public static String buildParametersList(ExecutableElement exec)
    {
        List<String> paramStrings = new ArrayList<String>();
        for (VariableElement param : exec.getParameters()) {
            paramStrings.add(buildVariableDecl(param));
        }
        return String.join(", ", paramStrings);
    }
    
    /**
     * Builds a `String` matching the declaration of the given variable element. For example, if
     * the given `var` represents a variable declared as
     * 
     *     Integer x
     *     
     * then this method would simply return "Integer x".
     * 
     * (Note that this is applicable/usable in the context of building the declaration of one of an
     * executable element's formal parameters.)
     */
    public static String buildVariableDecl(VariableElement var)
    {
        return Source.dropPackageName(var.asType().toString()) + " " + var.toString();
    }
    
    /**
     * Builds a `String` of the names of the formal parameters of the given executable, where each
     * name is separated by a comma. For example, if the given `exec` has the form
     * 
     *     public static void foo(int i, String str)
     *     
     * then this method would return "i, str".
     * 
     * If the given `exec` has no parameters, then the empty string is returned.
     */
    public static String buildParameterNamesList(ExecutableElement exec)
    {
        List<String> paramStrings = new ArrayList<String>();
        for (VariableElement var : exec.getParameters()) {
            paramStrings.add(var.toString());
        }
        return String.join(", ", paramStrings);
    }

    /**
     * Builds a `String` matching the type such that it is fully qualified and with any type
     * arguments dropped. For example, if the given `t` represents a declared type
     * 
     *     HashSet<Integer>
     * 
     * then this method would return "java.util.HashSet".
     */
    public static String buildWithoutTypeArgs(DeclaredType t)
    {
        String orig = t.toString();
        if (t.getTypeArguments().isEmpty()) {
            return orig;
        } else {
            return orig.substring(0, orig.indexOf('<'));
        }
    }
    
    /**
     * Builds a `String` of import declarations from the given set of types. Each of the given
     * `String` objects is assumed to be a fully qualified type that can be imported as-is.
     * For example, if the following set of types were passed,
     * 
     *     { java.util.HashSet, java.util.Set, java.lang.String }
     * 
     * then this method would return the following imports declarations as a single `String`:
     * 
     *     import java.util.HashSet;
     *     import java.util.Set;
     *     import java.lang.String;
     */
    public static String buildImportDecls(Iterable<String> imports)
    {
        String rv = "";
        for (String i : imports) {
            rv += "import " + i + ";\n";
        }
        return rv;
    }
    
    /**
     * Builds a `String` of import declarations collected from the given `TypeElement`; each of the
     * given `extraImports` will also be added as import declarations in the returned `String`.
     */
    public static String buildCollectedImportDecls(TypeElement t, String... extraImports)
    {
        return buildCollectedImportDecls(t, Arrays.asList(extraImports));
    }
   
    /**
     * Builds a `String` of import declarations collected from the given `TypeElement`; each of the
     * given `extraImports` will also be added as import declarations in the returned `String`.
     */
    public static String buildCollectedImportDecls(TypeElement t, Iterable<String> extraImports)
    {
        Set<String> imports = TypeCollector.collect(t);
        for (String s : extraImports) {
            imports.add(s);
        }
        return buildImportDecls(imports);
    }
    
}
