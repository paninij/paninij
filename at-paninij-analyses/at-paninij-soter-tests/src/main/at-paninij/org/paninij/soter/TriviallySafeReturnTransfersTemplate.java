package org.paninij.soter;

import org.paninij.lang.Capsule;


@Capsule
public class TriviallySafeReturnTransfersTemplate
{
    public Integer getInteger() {
        return new Integer(9);
    }

    public void giveInteger(Integer i) {
        // Nothing to do.
    }
}
