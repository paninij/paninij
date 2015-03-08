package org.paninij.runtime.ducks;

import org.paninij.runtime.ProcInvocation;
import org.paninij.runtime.ResolvableFuture;


public class void$Duck$Object implements ProcInvocation
{
    public final int panini$procID;
    public final Object panini$arg0;
   
    public void$Duck$Object(int procId, Object arg0) {
        panini$procId = procId;
        panini$arg0 = arg0;
    }

    @Override
    public int panini$procID() {
        return panini$procID;
    }
}
