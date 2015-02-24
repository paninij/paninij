package org.paninij.runtime;

public interface Capsule
{
    public void panini$start();
    public void panini$shutdown();
    public void panini$push(Object o);
    public void panini$join() throws java.lang.InterruptedException;
    public void panini$exit();
}
