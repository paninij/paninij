package org.paninij.proc.check.capsule.events;

import org.paninij.lang.Capsule;
import org.paninij.lang.PaniniEvent;
import org.paninij.lang.Local;
import org.paninij.lang.Broadcast;

@Capsule
public class EventWithLocalTemplate
{
    @Broadcast @Local public PaniniEvent<String> event;
}
