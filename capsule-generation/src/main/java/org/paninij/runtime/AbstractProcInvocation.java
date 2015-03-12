package org.paninij.runtime;

public class AbstractProcInvocation implements ProcInvocation
{
    private int procID;

    public AbstractProcInvocation(int procID) {
        this.procID = procID;
    }

    @Override
    public int panini$procID() {
        return procID;
    }

}
