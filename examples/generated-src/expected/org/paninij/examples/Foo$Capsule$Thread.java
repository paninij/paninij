package org.paninij.examples.signatures;

import org.paninij.runtime.Capsule$Thread;
import org.paninij.runtime.Panini.Message;

/**
 * This capsule was auto-generated from `org.paninij.examples.signatures.Foo`
 */
public class Foo$Capsule$Thread extends Capsule$Thread implements Foo$Capsule
{
    //procedure enum for unwrapping ducks
    public static final int panini$proc$setGreeting$String = 0;
    public static final int panini$proc$getObject = 1;

    private Foo panini$Template;

    public Foo$Capsule$Thread
    {
        super();
        panini$Template = new Foo();
    }

    public void setGreeting(java.lang.String greeting)
    {
        void$Duck$Object$Thread panini$duck$future = null;
        panini$duck$future = new void$Duck$Object$Thread(panini$proc$setGreeting$String, greeting);
        panini$push(panini$duck$future);
    }

    public Object getObject()
    {
        java_lang_Object$Duck$$Thread panini$duck$future = null;
        panini$duck$future = new java_lang_Object$Duck$$Thread(panini$proc$getObject);
        panini$push(panini$duck$future);
        return panini$duck$future;
    }
    
    public final void run()
    {
        try
        {
            // panini$wire$sys();
            // panini$capsule$init();
            boolean panini$terminate = false;
            while (!panini$terminate)
            {
                Message panini$duck$future = get$Next$Duck();
                switch (panini$duck$future.panini$msgID()) {
                case panini$proc$setGreeting$String:
                    // TODO
                    break
                case panini$proc$getObject;
                    // TODO
                    break;
                case -1:
                    if (this.panini$size > 0)
                    {
                        panini$push(panini$duck$future);
                        break;
                    }
                case -2:
                    panini$terminate = true;
                    break;
                }
            }
        }
    }

}
