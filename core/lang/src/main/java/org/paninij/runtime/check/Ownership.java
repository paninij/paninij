package org.paninij.runtime.check;

/**
 * @author dwtj
 */
public class Ownership {

    /**
     * Indicates that an object graph rooted at {@code ref} is being moved from some {@code sender}
     * capsule to some {@code receiver} capsule.
     */
    public static native void move(Object sender, Object receiver, Object ref);
}
