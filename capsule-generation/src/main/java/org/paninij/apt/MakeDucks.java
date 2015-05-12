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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.paninij.apt.util.DuckShape;
import org.paninij.apt.util.PaniniModelInfo;

public class MakeDucks
{
    PaniniProcessor context;
    TypeElement template;

    public static MakeDucks make(PaniniProcessor context, TypeElement template)
    {
        MakeDucks m = new MakeDucks();
        m.context = context;
        m.template = template;
        return m;
    }

    /**
     * Make a duck source file for each novel duck shape found on the `template` class and for each
     * threading profile.
     */
    public void makeDucks()
    {
        Set<DuckShape> duckShapes = getAllDuckShapes();
        duckShapes.removeAll(context.foundDuckShapes);
        List<MakeDuck> profiles = new ArrayList<MakeDuck>(4);
        profiles.add(MakeDuck$Thread.make(context));
        //profiles.add(MakeDuck$Monitor.make(context));
        //profiles.add(MakeDuck$Serial.make(context));
        //profiles.add(MakeDuck$Task.make(context));

        for(DuckShape currentDuck : duckShapes)
        {
            for(MakeDuck currentProfile : profiles)
            {
                currentProfile.makeSourceFile(currentDuck);
            }
        }

        context.foundDuckShapes.addAll(duckShapes);
    }
    /**
     * Gets duck shapes of all methods of the template class.
     * @return
     */
    private Set<DuckShape> getAllDuckShapes()
    {
        Set<DuckShape> duckShapes = new HashSet<DuckShape>();

        for(Element elem : this.template.getEnclosedElements())
        {
            if(PaniniModelInfo.isProcedure(elem))
            {
                ExecutableElement method = (ExecutableElement) elem;
                duckShapes.add(new DuckShape(method));
            }
        }
        return duckShapes;
    }
}
