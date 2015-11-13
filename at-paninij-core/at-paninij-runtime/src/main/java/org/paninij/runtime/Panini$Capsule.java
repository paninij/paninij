
package org.paninij.runtime;

public interface Panini$Capsule
{
    public void panini$start();
    public void panini$push(Object o);
    public void panini$join() throws java.lang.InterruptedException;
    public void panini$openLink();
    public void panini$closeLink();
    public void exit();
    public void yield(long millis);
}
