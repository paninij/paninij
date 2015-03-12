package org.paninij.runtime.ducks;

import org.paninij.runtime.Panini$Message;
import org.paninij.runtime.ResolvableFuture;


public class void$Duck$Object implements Panini$Message
{
    public final int panini$procID;
    public final Object panini$arg0;
   
    public void$Duck$Object(int procID, Object arg0) {
        panini$procId = procID;
        panini$arg0 = arg0;
    }

    @Override
    public int panini$msgID() {
        return panini$procID;
    }
}
