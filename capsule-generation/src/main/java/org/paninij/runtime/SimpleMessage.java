package org.paninij.runtime;

public class SimpleMessage implements Panini$Message
{
    private int procID;

    public SimpleMessage(int procID) {
        this.procID = procID;
    }

    @Override
    public int panini$msgID() {
        return procID;
    }

}
