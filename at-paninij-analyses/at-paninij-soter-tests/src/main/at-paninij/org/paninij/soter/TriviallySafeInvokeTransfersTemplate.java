package org.paninij.soter;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule
public class TriviallySafeInvokeTransfersTemplate
{
    @Local TriviallySafeReturnTransfers server;
    
    public void run()
    {
        Integer i = server.getInteger();
        server.giveInteger(i);
    }
}
