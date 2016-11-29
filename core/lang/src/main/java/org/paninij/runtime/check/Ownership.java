package org.paninij.runtime.check;

import org.paninij.runtime.Panini$Capsule;

/**
 * @author dwtj
 */
public class Ownership {

    /**
     * Indicates that a set of object graphs are being being moved from some {@code sender} capsule
     * to some {@code receiver} capsule. The given {@code senderEncapsulated} reference is
     * expected to point to the capsule template instance encapsulated by {@code sender}. Each
     * {@code moved} reference points to an object graph being moved.
     */
    public static native void move(Panini$Capsule sender, Object senderEncapsulated,
                                   Panini$Capsule receiver, Object... moved);
}
