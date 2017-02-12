package org.paninij.proc.check.capsule.events;

import org.paninij.lang.Capsule;
import org.paninij.lang.PaniniEvent;
import org.paninij.lang.Imports;
import org.paninij.lang.Broadcast;

@Capsule
public class EventWithImportsTemplate
{
    @Broadcast @Imports public PaniniEvent<String> event;
}
