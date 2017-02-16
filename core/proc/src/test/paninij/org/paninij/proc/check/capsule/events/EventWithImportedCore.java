package org.paninij.proc.check.capsule.events;

import org.paninij.lang.Capsule;
import org.paninij.lang.PaniniEvent;
import org.paninij.lang.Imported;
import org.paninij.lang.Broadcast;

@Capsule
public class EventWithImportedCore
{
    @Broadcast @Imported public PaniniEvent<String> event;
}
