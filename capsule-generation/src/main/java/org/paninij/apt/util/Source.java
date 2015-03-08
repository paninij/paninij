package org.paninij.apt.util;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

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
        LITERAL, // The last-seen character was a string-literal (or the scan
                 // just started).
        HASH, // The last-seen character was '#'.
        HASH_NUM, // The last-seen character was part of a '#'-number
                  // identifier.
    }

    /**
     * Inserts any elements in the given list of items into the format string at
     * format elements. A format element is a substring of `fmt` that satisfies
     * the pattern "#\d+", that is, a hash- symbol followed by one or more
     * digits. The digits of a format element are interpreted to be the index
     * into `items` used to find which item to place at that location. For
     * example,
     *
     * Source.format("#0? #1, #0...", "World", "Hello") ->
     * "World? Hello, World..."
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

    public static String buildModifierString(ExecutableElement exec)
    {
        List<String> modifiers = new ArrayList<String>();
        for (Modifier m : exec.getModifiers())
        {
            modifiers.add(m.toString());
        }
        return String.join(" ", modifiers);
    }

    public static String buildParameterList(ExecutableElement exec)
    {
        List<String> paramStrings = new ArrayList<String>();
        for (VariableElement param : exec.getParameters()) {
            paramStrings.add(buildParamDecl(param));
        }
        return String.join(", ", paramStrings);
    }
    
    public static String buildParamDecl(VariableElement param)
    {
        return Source.dropPackageName(param.asType().toString()) + " " + param.toString();
    }
    
    public static String buildArgsList(ExecutableElement method)
    {
        List<String> paramStrings = new ArrayList<String>();
        for (VariableElement var : method.getParameters()) {
            paramStrings.add(var.toString());
        }
        return String.join(", ", paramStrings);
    }
    
    
}
