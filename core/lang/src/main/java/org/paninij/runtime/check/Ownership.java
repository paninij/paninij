package org.paninij.runtime.check;

import org.paninij.runtime.Panini$Capsule;

/**
 * @author dwtj
 */
public class Ownership {

    /**
     * Indicates that an object graph rooted at {@code ref} is being moved from some {@code sender}
     * capsule to some {@code receiver} capsule.
     */
    public static native void move(Panini$Capsule sender, Panini$Capsule receiver, Object ref);
}
