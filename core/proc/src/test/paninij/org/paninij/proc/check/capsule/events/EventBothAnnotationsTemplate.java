package org.paninij.proc.check.capsule.events;

import org.paninij.lang.Capsule;
import org.paninij.lang.PaniniEvent;
import org.paninij.lang.Chain;
import org.paninij.lang.Broadcast;

@Capsule
public class EventBothAnnotationsTemplate
{
    @Chain @Broadcast public PaniniEvent<String> event;
}
