package org.paninij.lang;

import static java.text.MessageFormat.format;

import java.lang.String;

import org.paninij.runtime.Capsule$Mockup;
import org.paninij.runtime.Capsule$Monitor;
import org.paninij.runtime.Capsule$Serial;
import org.paninij.runtime.Capsule$Task;
import org.paninij.runtime.Capsule$Thread;
import org.paninij.runtime.Panini$Capsule;


/**
 * Helper class for looking up various capsule-related `Class<?>` objects and instantiating
 * capsule instances.
 */
public class CapsuleFactory
{
    public final Class<Panini$Capsule>  capsuleInterface;
    public final Class<Capsule$Mockup>  capsuleMockup;
    public final Class<Capsule$Monitor> capsuleMonitor;
    public final Class<Capsule$Serial>  capsuleSerial;
    public final Class<Capsule$Task>    capsuleTask;
    public final Class<Capsule$Thread>  capsuleThread;

    @SuppressWarnings("unchecked")
    public CapsuleFactory(Class<? extends Panini$Capsule> clazz) throws ClassNotFoundException
    {
        capsuleInterface = (Class<Panini$Capsule>) clazz;
        
        capsuleMockup  = (Class<Capsule$Mockup>)  Class.forName(clazz.getName() + "$Mockup");
        capsuleMonitor = (Class<Capsule$Monitor>) Class.forName(clazz.getName() + "$Monitor");
        capsuleSerial  = (Class<Capsule$Serial>)  Class.forName(clazz.getName() + "$Serial");
        capsuleTask    = (Class<Capsule$Task>)    Class.forName(clazz.getName() + "$Task");
        capsuleThread  = (Class<Capsule$Thread>)  Class.forName(clazz.getName() + "$Thread");
    }

    public Capsule$Mockup newMockupInstance() {
        return newInstance(capsuleMockup);
    }

    public Capsule$Monitor newMonitorInstance() {
        return newInstance(capsuleMonitor);
    }

    public Capsule$Serial newSerialInstance() {
        return newInstance(capsuleSerial);
    }

    public Capsule$Task newTaskInstance() {
        return newInstance(capsuleTask);
    }

    public Capsule$Thread newThreadInstance() {
        return newInstance(capsuleThread);
    }
    

    public Panini$Capsule newInstance(ExecutionProfile profile)
    {
        switch (profile) {
        case MOCKUP:
            return newMockupInstance();
        case MONITOR:
            return newMonitorInstance();
        case SERIAL:
            return newSerialInstance();
        case TASK:
            return newTaskInstance();
        case THREAD:
            return newThreadInstance();
        default:
            throw new IllegalArgumentException("Unknown execution profile: " + profile);
        }
    }
    
    public Class<? extends Panini$Capsule> getInstantiableClass(ExecutionProfile profile)
    {
        switch (profile) {
        case MOCKUP:
            return capsuleMockup;
        case MONITOR:
            return capsuleMonitor;
        case SERIAL:
            return capsuleSerial;
        case TASK:
            return capsuleTask;
        case THREAD:
            return capsuleThread;
        default:
            throw new IllegalArgumentException("Unknown execution profile: " + profile);
        }
    }

    private static <T> T newInstance(Class<? extends T> clazz)
    {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            String err = "Failed to create a new instance of `{0}`.";
            throw new RuntimeException(format(err, clazz.getName()), ex);
        }
    }
}