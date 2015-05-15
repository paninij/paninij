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

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import org.paninij.apt.util.DuckShape;
import org.paninij.apt.util.Source;
import org.paninij.apt.util.TypeCollector;

public class MakeDuck$Thread extends MakeDuck
{
    public static MakeDuck$Thread make(PaniniProcessor context)
    {
        MakeDuck$Thread m = new MakeDuck$Thread();
        m.context = context;
        return m;
    }


    @Override
    public void makeSourceFile(DuckShape currentDuck)
    {
        try {
            context.createJavaFile(buildQualifiedClassName(currentDuck),
                                   buildDuck(currentDuck));
        } catch (UnsupportedOperationException ex) {
            context.warning(ex.toString());
        }
    }


    @Override
    String buildNormalDuck(DuckShape currentDuck)
    {
        String src = Source.cat(0,
                "package #0;",
                "",
                "##",
                "",
                "public class #2 extends #4 implements Panini$Message, Panini$Future<#4>",
                "{",
                "    public final int panini$procID;",
                "    private #4 panini$result = null;",
                "    boolean panini$isResolved = false;",
                "",
                "    ##",
                "",
                "    ##",
                "",
                "    @Override",
                "    public int panini$msgID() {",
                "        return panini$procID;",
                "    }",
                "",
                "    @Override",
                "    public void panini$resolve(#4 result) {",
                "        synchronized (this) {",
                "            panini$result = result;",
                "            panini$isResolved = true;",
                "            this.notifyAll();",
                "        }",
                "        ##",
                "    }",
                "",
                "    @Override",
                "    public #4 panini$get() {",
                "        while (panini$isResolved == false) {",
                "            try {",
                "                synchronized (this) {",
                "                    while (panini$isResolved == false) this.wait();",
                "                }",
                "            } catch (InterruptedException e) { /* try waiting again */ }",
                "         }",
                "         return panini$result;",
                "    }",
                "",
                "    /* The following override the methods of `#4` */",
                "    ##",
                "}");

        src = Source.format(src, this.buildPackage(currentDuck),
                                 null,
                                 this.buildClassName(currentDuck),
                                 null,
                                 currentDuck.getSimpleReturnType(),
                                 null,
                                 null,
                                 null);

        src = Source.formatAligned(src, buildImports(currentDuck));
        src = Source.formatAligned(src, buildParameterFields(currentDuck));
        src = Source.formatAligned(src, buildConstructor(currentDuck));
        src = Source.formatAligned(src, buildReleaseArgs(currentDuck));
        src = Source.formatAligned(src, buildFacades(currentDuck));

        return src;
    }


    @Override
    String buildVoidDuck(DuckShape currentDuck)
    {
        String src = Source.cat(0, "package #0;",
                                   "",
                                   "import org.paninij.runtime.Panini$Message;",
                                   "",
                                   "public class #1 implements Panini$Message",
                                   "{",
                                   "    public final int panini$procID;",
                                   "",
                                   "    ##",
                                   "",
                                   "    ##",
                                   "",
                                   "    @Override",
                                   "    public int panini$msgID() {",
                                   "        return panini$procID;",
                                   "    }",
                                   "}");

        src = Source.format(src, buildPackage(currentDuck), buildClassName(currentDuck));
        src = Source.formatAligned(src, buildParameterFields(currentDuck));
        src = Source.formatAligned(src, buildConstructor(currentDuck));
        return src;
    }


    @Override
    String buildPaniniCustomDuck(DuckShape currentDuck)
    {
        // TODO: Make this handle more than just `String`.
        assert(currentDuck.returnType.toString().equals("org.paninij.lang.String"));

        String src = Source.cat(0, "package #0;",
                                   "",
                                   "import org.paninij.lang.String;",
                                   "",
                                   "public class #1 extends String",
                                   "{",
                                   "    private int panini$procID;",
                                   "",
                                   "    ##",
                                   "",
                                   "}");
        src = Source.format(src, buildPackage(currentDuck),
                                 buildClassName(currentDuck));
        src = Source.formatAligned(src, buildConstructor(currentDuck, "super(\"\");"));
        return src;
    }


    List<String> buildImports(DuckShape currentDuck)
    {
        TypeElement typeElem = (TypeElement) ((DeclaredType) currentDuck.returnType).asElement();
        return Source.buildCollectedImportDecls(typeElem, currentDuck.getQualifiedReturnType(),
                                                          "org.paninij.runtime.Panini$Message",
                                                          "org.paninij.runtime.Panini$Future");
    }


    @Override
    String buildClassName(DuckShape currentDuck)
    {
        return currentDuck.toString() + "$Thread";
    }


    @Override
    String buildQualifiedClassName(DuckShape currentDuck)
    {
        return buildPackage(currentDuck) + "." + currentDuck.toString() + "$Thread";
    }


    @Override
    List<String> buildConstructor(DuckShape currentDuck)
    {
        return buildConstructor(currentDuck, "");
    }


    List<String> buildConstructor(DuckShape currentDuck, String prependToBody)
    {
        // Create a list of parameters to the constructor starting with the `procID`.
        List<String> params = new ArrayList<String>();
        params.add("int procID");
        for(int idx = 0; idx < currentDuck.slotTypes.size(); idx++) {
            params.add(currentDuck.slotTypes.get(idx) + " arg" + idx);
        }
        
        // Create a list of initialization statements.
        List<String> initializers = new ArrayList<String>();
        initializers.add("panini$procID = procID;");
        for (int idx = 0; idx < currentDuck.slotTypes.size(); idx++) {
            initializers.add(Source.format("panini$arg#0 = arg#0;", idx));
        }
        
        List<String> src = Source.linesList(0, "public #0(#1)",
                                               "{",
                                               "    #2",
                                               "    ##",
                                               "}");

        src = Source.formatList(src, buildClassName(currentDuck),
                                     String.join(", ", params),
                                     prependToBody);
        src = Source.formatAlignedList(src, initializers);

        return src;
    }
}
