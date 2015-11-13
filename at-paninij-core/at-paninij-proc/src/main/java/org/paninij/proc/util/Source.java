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

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * This provides functionality similar to the `MessageFormat`, but with some features that make it
 * simpler to use when constructing source code.
 */
public class Source
{
    public static String tab(int depth, String line)
    {
        final String FOUR_SPACES =   "    ";
        final String EIGHT_SPACES =  "        ";
        final String TWELVE_SPACES = "            ";
        
        
        if (depth < 0) {
            String msg = "`depth` must not be negative, but given value was " + depth + ".";
            throw new IllegalArgumentException(msg);
        }
        
        switch (depth) {
        case 0:
            return line;
        case 1:
            return FOUR_SPACES + line;
        case 2:
            return EIGHT_SPACES + line;
        case 3:
            return TWELVE_SPACES + line;
        default:
            String spaces = "";
            for (int i = 0; i < depth; i++) {
                spaces += FOUR_SPACES;
            }
            return spaces + line;
        }
    }
    
    
    /**
     * Concatenates each of the lines, separating each with a single '\n' character.
     */
    public static String cat(String... lines)
    {
        return Source.cat(0, lines);
    }
    
    
    /**
     * Concatenates each of the lines, separating each with a single '\n' character and tabbing
     * each line over the specified `depth`.
     */
    public static String cat(int depth, String... lines)
    {
        String[] tabbed = new String[lines.length];
        for (int i = 0; i < lines.length; i++) {
            tabbed[i] = Source.tab(depth, lines[i]);
        }

        return String.join("\n", tabbed) + "\n";
    }
    
     /**
     * A helper method for turning a variable-length method call into a list of strings.
     */
    public static List<String> lines(String... lines)
    {
        return Source.lines(0, lines);
    }
   
    /**
     * A helper method for turning a variable-length method call into a list of strings, where each
     * line has been tabbed to the given depth.
     */
    public static List<String> lines(int depth, String... lines)
    {
        List<String> rv = new ArrayList<String>();
        for (String line : lines) {
            rv.add(tab(depth, line));
        }
        return rv;
    }

    
    /**
     * Inserts each of the given `lines` into the `fmt` string at the first "##" such that each
     * line is inserted at the same depth as the "##". For example,
     * 
     *     formatAligned("    ##", "foo", "bar", "baz") -> "    foo\n    bar\n    baz"
     * 
     * Technically, the portion of the line which precedes the "##" will be copied as the prefix of
     * each of the lines being inserted.
     * 
     * Note that if `lines` is empty, then this method will return a string just like `fmt`, except
     * with the first "##" characters removed.
     * 
     * @throws `InvalidArgumentException` if any character of prefix is not a whitespace character.
     */
    public static String formatAligned(String fmt, Object... lines)
    {
        final String FORMAT_ELEMENT_SYMBOL = "##";
        final int hashStartIndex = fmt.indexOf(FORMAT_ELEMENT_SYMBOL);
        final int hashEndIndex = hashStartIndex + FORMAT_ELEMENT_SYMBOL.length();
        
        final String linePrefix = getWhitespaceLinePrefix(fmt, hashStartIndex);
        
        final String fmtPrefix = fmt.substring(0, hashStartIndex);
        final String fmtSuffix = fmt.substring(hashEndIndex);

        // Note that the `linePrefix` is used to separate each stringified line.
        String[] strings = new String[lines.length];
        for(int i = 0; i < lines.length; i++)
        {
        	strings[i] = lines[i].toString();
        }
        return fmtPrefix + String.join("\n" + linePrefix, strings) + fmtSuffix;
    }
    
    
    /**
     * A helper method for `formatAligned()` that returns the line prefix before `idx`, that is,
     * the substring which spans from the beginning of the line that `idx` points into up to the
     * given `idx`. For example,
     * 
     *     getWhitespaceLinePrefix("    foo", 4) -> "    "
     * 
     * Note that this method will throw an `IllegalArgumentException` if the line prefix is not
     * all whitespace characters.
     */
    private static String getWhitespaceLinePrefix(String str, int idx)
    {
        // Scan back the start of this line or the beginning of the entire `fmt` string, whichever
        // comes first. Use this index to extract the line prefix.
        int lineStartIndex = idx;
        char c;
        char[] chars = str.toCharArray();
        while (lineStartIndex > 0 && (c = chars[lineStartIndex - 1]) != '\n')
        {
            if (Character.isWhitespace(c) == false)
            {
                String msg = "Line prefix preceeding `idx` must only contain whitespace.";
                throw new IllegalArgumentException(msg);
            }
            lineStartIndex--;
        }
        return str.substring(lineStartIndex, idx);
    }


    public static String formatAligned(String fmt, List<String> items)
    {
        return formatAligned(fmt, items.toArray());
    }
    
    
    /**
     * Applies `formatAligned()` to the first format string in `fmts` which contains "##", and
     * includes each of the lines which have been expanded into the returned list. For example, if
     * `fmts` is defined as a list containing the following format strings,
     * 
     *     ["public void foo() {",
     *      "    fooom();",
     *      "    ##"
     *      "}"]
     *  
     *  then
     * 
     *     formatAlignedFirst(fmts, "bar();", "baz();")
     *  
     *  would evaluate to a list containing the following strings:
     *  
     *     ["public void foo() {",
     *      "    fooom();",
     *      "    bar();",
     *      "    baz();",
     *      "}"]
     */
    public static List<String> formatAlignedFirst(List<String> fmts, Object... items)
    {
        List<String> lines = new ArrayList<String>();

        boolean foundHashes = false;
        for (String fmt : fmts)
        {
            if (foundHashes == false && fmt.contains("##"))
            {
                // `fmt` is the first string in `fmts` to contain "##".
                String formatted = formatAligned(fmt, items);
                for (String line : formatted.split("\n")) {
                    lines.add(line);
                }
                foundHashes = true;
            }
            else
            {
                // The current `fmt` is either a string in `fmts` which comes before or after the
                // first string the list which contains "##".
                lines.add(fmt);
            }
        }
        
        return lines;
    }


    public static List<String> formatAlignedFirst(List<String> fmts, List<String> items)
    {
        return formatAlignedFirst(fmts, items.toArray());
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
     * the pattern "#\d+", that is, a hash-symbol followed by one or more
     * digits. The digits of a format element are interpreted to be the index
     * into `items` used to find which item to place at that location. For
     * example,
     *
     *     Source.format("#0? #1, #0...", "World", "Hello") -> "World? Hello, World..."
     */
    public static String format(String fmt, Object... items)
    {
        // Make its initial capacity comparable to the length of the given `fmt`.
        StringBuilder result = new StringBuilder(fmt.length());

        // A temporary to hold format element indices (without '#'-prefix) as `fmt` is scanned.
        String idxStr = "";

        FormatState state = FormatState.LITERAL;
        for (char c : fmt.toCharArray())
        {
            switch (state) {
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
                    appendItem(idxStr, items, result);
                    if (c == '#') {
                        state = FormatState.HASH;
                    } else {
                        result.append(c);
                        state = FormatState.LITERAL;
                    }
                }
                continue;
            }
        }
        
        // Finished consuming the format string. Perform last append if necessary.
        switch (state) {
        case LITERAL:
            // Nothing to do, since last literal character was already appended.
            break;
        case HASH:
            // The last character was '#', so it was consumed during above for-loop. Append it now.
            result.append('#');
            break;
        case HASH_NUM:
            // Interpret the last characters as a format element.
            appendItem(idxStr, items, result);
            break;
        }

        return result.toString();
    }
    
    
    /**
     * A helper method for `format()`. Appends the `String` representation of object in `items`
     * indicated by `idxStr` to the end of the given `StringBuilder`.
     * 
     * Note that this method attempts to convert the String `idxStr` to an `int` value using
     * `Integer.parseInt()`. That method throws a `NumberFormatException` "if the string does not
     * contain a parsable integer".
     * 
     * This method throws an `IllegalArgumentException` if the parsed value of `idx` is not a valid
     * index into `items`.
     */
    private static void appendItem(String idxStr, Object[] items, StringBuilder builder)
    {
        int idx = Integer.parseInt(idxStr);
        if (idx >= items.length)
        {
            String msg = "The format element's index, " + idxStr + ", is too large.";
            throw new IllegalArgumentException(msg);
        }
        builder.append(items[idx]);
    }
    
    
    /**
     * Applies the appropriate `format()` to each of the strings of the list, and returns the
     * result as another list of strings. (Note that function application is not done in-place.)
     */
    public static List<String> formatAll(List<String> fmts, Object... items)
    {
        List<String> rv = new ArrayList<String>(fmts.size());
        for (String fmt : fmts) {
            rv.add(Source.format(fmt, items));
        }
        return rv;
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
        String decl = format("#0 #1 #2(#3)", buildModifiersList(exec),
                                             exec.getReturnType(),
                                             exec.getSimpleName(),
                                             buildParametersList(exec));

        List<String> thrown = new ArrayList<String>();
        for (TypeMirror type : exec.getThrownTypes()) {
            System.out.println("throws");
            thrown.add(type.toString());
        }

        return (thrown.isEmpty()) ? decl : decl + " throws " + String.join(", ", thrown);
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
     * Builds a `List<String>` of import declarations from the given set of types. Each of the
     * given `String` objects is assumed to be a fully qualified type that can be imported as-is.
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
    public static List<String> buildImportDecls(Iterable<String> imports)
    {
        List<String> rv = new ArrayList<String>();
        for (String i : imports) {
            rv.add("import " + i + ";");
        }
        return rv;
    }

    /**
     * Builds a `String` of import declarations collected from the given `TypeElement`; each of the
     * given `extraImports` will also be added as import declarations in the returned `String`.
     */
    public static List<String> buildCollectedImportDecls(TypeElement t, String... extraImports)
    {
        return buildCollectedImportDecls(t, Arrays.asList(extraImports));
    }

    /**
     * Builds a `String` of import declarations collected from the given `TypeElement`; each of the
     * given `extraImports` will also be added as import declarations in the returned `String`.
     */
    public static List<String> buildCollectedImportDecls(TypeElement t,
                                                         Iterable<String> extraImports)
    {
        Set<String> imports = TypeCollector.collect(t);
        for (String s : extraImports) {
            imports.add(s);
        }
        return buildImportDecls(imports);
    }

}
