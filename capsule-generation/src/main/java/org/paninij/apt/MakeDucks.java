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

public class MakeDucks
{

    PaniniPress context;
    TypeElement template;
    
    public static MakeDucks make(PaniniPress context, TypeElement template)
    {
        // TODO Auto-generated method stub
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
        
        for(Element el : this.template.getEnclosedElements())
        {
            if(el.getKind() == ElementKind.METHOD)
            {
                ExecutableElement method = (ExecutableElement) el;
                duckShapes.add(DuckShape.make(this.context, method));
            }
        }
        return duckShapes;
    }

}
