package org.paninij.runtime.check;

import org.paninij.runtime.Panini$Capsule;

/**
 * @author dwtj
 */
public class Ownership {

    /**
     * Indicates that an object graph rooted at {@code ref} is being moved from some {@code sender}
     * capsule to some {@code receiver} capsule. The given {@code senderEncapsulated} reference is
     * expected to point to the capsule template instance encapsulated by {@code sender}.
     */
    public static native void move(Panini$Capsule sender, Object senderEncapsulated,
                                   Panini$Capsule receiver, Object ref);

}
