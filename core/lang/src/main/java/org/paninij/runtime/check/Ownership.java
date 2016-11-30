package org.paninij.runtime.check;

import org.paninij.runtime.Capsule$Thread;

/**
 * @author dwtj
 */
public class Ownership {

    /**
     * Indicates that a set of object graphs is being moved via procedure invocation. More
     * specifically, this indicates that some {@code sender} capsule is invoking a procedure on some
     * {@code receiver} capsule and passing the {@code moved} object graphs as arguments.
     *
     * @param sender
     *          The capsule which is sending these object graphs.
     * @param senderEncapsulated
     *          The capsule template instance encapsulated by the {@code sender} capsule.
     * @param receiver
     *          The capsule which will receive these object graphs once the move has been checked.
     * @param moved
     *          Zero or more reference values (i.e. objects and/or arrays) being moved from {@code
     *          sender} capsule to {@code receiver} capsule. The object graphs reachable from these
     *          references are also considered moved.
     *
     * @throws OwnershipMoveError
     *          If an object in the moved object graphs is found to be reachable from the {@code
     *          sender} capsule.
     */
    public static native void procedureInvocationMove(Capsule$Thread sender,
                                                      Object senderEncapsulated,
                                                      Capsule$Thread receiver,
                                                      Object... moved);


    /**
     * Indicates that an object graph is being moved via procedure return. More specifically, this
     * indicates that some {@code sender} capsule has completed the procedure invocation requested
     * by some other capsule and the result (i.e. return value) of this invocation is being moved
     * to the invoking capsule.
     *
     * @param sender
     *          The capsule which is sending these object graphs.
     * @param senderEncapsulated
     *          The capsule template instance encapsulated by the {@code sender} capsule.
     * @param moved
     *          A reference value (i.e. an object and/or array) being moved from the {@code sender}
     *          capsule to the invoking capsule. The object graph reachable from this reference is
     *          also considered moved.
     *
     * @throws OwnershipMoveError
     *          If an object in the moved object graph is found to be reachable from the {@code
     *          sender} capsule.
     */
    public static native void procedureReturnMove(Capsule$Thread sender,
                                                  Object senderEncapsulated,
                                                  Object moved);
}
