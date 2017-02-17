package org.paninij.proc.check.capsule.events;

import org.paninij.lang.Capsule;
import org.paninij.lang.PaniniEvent;
import org.paninij.lang.Imported;
import org.paninij.lang.Broadcast;

@Capsule
class EventWithImportedCore
{
    @Broadcast @Imported PaniniEvent<String> event;
}
