package org.paninij.runtime.ducks;

import org.paninij.runtime.Panini$Message;
import org.paninij.runtime.ResolvableFuture;


public class Object$Duck$Object extends Object implements Panini$Message, ResolvableFuture<Object>
{
    public final int panini$procID;
    public final Object panini$arg0;

    private Object panini$result = null;
   
    public Object$Duck$void(int procId, Object arg0) {
        panini$procId = procId;
        panini$arg0 = arg0;
    }

    @Override
    public int panini$msgID() {
        return panini$procID;
    }
    
    @Override
    public panini$resolve(Object result)
    {
        synchronized (this) {
            panini$result = result;
            panini$isResolved = true;
            this.notifyAll();
        }

        // Release args:
        // <none>
    }
    
    @Override
    public Object panini$get()
    {
        while (panini$isResolved == false) {
            try {
                synchronized (this) {
                    while (panini$isResolved == false) this.wait();
                }
            } catch (InterruptedException e) { /* Try waiting again. */ }
        }
        return panini$result;
    }
    

    /* The following methods override the methods of `Object` */
    @Override
    protected Object clone() {
        return panini$get().clone();
    }
    
    @Override
    public final int hashCode() {
        return panini$get().hashCode();
    }

    @Override
    public final boolean equals(Object o) {
        return panini$get().equals(o);
    }
    
    // TODO: More methods of `Object`
}