package org.paninij.examples.signatures;

import org.paninij.runtime.Capsule$Thread;

/**
 * This capsule was auto-generated from `org.paninij.examples.signatures.Foo`
 */
public class Foo$Capsule$Thread extends Capsule$Thread implements Foo$Capsule
{
    //procedure enum for unwrapping ducks
    public static final int panini$methodConst$setGreetingParam$String = 0;
    public static final int panini$methodConst$getObject = 1;
    
    //State Variables
    private String greeting;
    
    private final void setGreeting$Original(java.lang.String greeting) 
    {
        this.greeting = greeting;
    }
    
    private final Object getObject$Original() 
    {
        return new Object();
    }

    public void setGreeting(java.lang.String greeting)
    {
        void$Duck$Object$Thread panini$duck$future = null;
        panini$duck$future = new void$Duck$Object$Thread(panini$methodConst$setGreetingParam$String, greeting);
        panini$push(panini$duck$future);
    }
    
    public Object getObject()
    {
        java_lang_Object$Duck$$Thread panini$duck$future = null;
        panini$duck$future = new java_lang_Object$Duck$$Thread(panini$methodConst$getObject);
        panini$push(panini$duck$future);
        return panini$duck$future;
    }

}
