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
    private final static Class<Panini$Capsule> PANINI_CAPSULE = lookupPaniniCapsuleClass();
    private final static Class<CapsuleInterface> CAPSULE_INTERFACE = lookupCapsuleInterfaceClass();


    public final Class<Panini$Capsule>  capsuleInterface;
    public final Class<Capsule$Mockup>  capsuleMockup;
    public final Class<Capsule$Monitor> capsuleMonitor;
    public final Class<Capsule$Serial>  capsuleSerial;
    public final Class<Capsule$Task>    capsuleTask;
    public final Class<Capsule$Thread>  capsuleThread;


    @SuppressWarnings("unchecked")
    public CapsuleFactory(String capsuleName) throws ClassNotFoundException
    {
        Class<?> clazz = Class.forName(capsuleName);

        if (!isCapsuleInterface(clazz)) {
            String err = "`{0}` is not a valid capsule.";
            throw new IllegalArgumentException(format(err, clazz.getName()));
        } 

        capsuleInterface = (Class<Panini$Capsule>)  clazz;

        // TODO: Consider refactoring this class to instead perform these class lookups lazily.
        capsuleMockup    = (Class<Capsule$Mockup>)  Class.forName(clazz.getName() + "$Mockup");
        capsuleMonitor   = (Class<Capsule$Monitor>) Class.forName(clazz.getName() + "$Monitor");
        capsuleSerial    = (Class<Capsule$Serial>)  Class.forName(clazz.getName() + "$Serial");
        capsuleTask      = (Class<Capsule$Task>)    Class.forName(clazz.getName() + "$Task");
        capsuleThread    = (Class<Capsule$Thread>)  Class.forName(clazz.getName() + "$Thread");
    }
    
    @SuppressWarnings("unchecked")
    public CapsuleFactory(Class<?> clazz) throws ClassNotFoundException
    {
        
        if(!isCapsuleInterface(clazz)) {
            String err = "`{0}` is not a valid capsule.";
            throw new IllegalArgumentException(format(err, clazz.getName()));
        }
        
        capsuleInterface = (Class<Panini$Capsule>) clazz;
        
        capsuleMockup    = (Class<Capsule$Mockup>)  Class.forName(clazz.getName() + "$Mockup");
        capsuleMonitor   = (Class<Capsule$Monitor>) Class.forName(clazz.getName() + "$Monitor");
        capsuleSerial    = (Class<Capsule$Serial>)  Class.forName(clazz.getName() + "$Serial");
        capsuleTask      = (Class<Capsule$Task>)    Class.forName(clazz.getName() + "$Task");
        capsuleThread    = (Class<Capsule$Thread>)  Class.forName(clazz.getName() + "$Thread");
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


    public static boolean isCapsuleInterface(Class<?> clazz)
    {
        return clazz.isInterface()
            && PANINI_CAPSULE.isAssignableFrom(clazz)
            && clazz.isAnnotationPresent(CAPSULE_INTERFACE);
    }


    private static Class<?> lookupRuntimeClass(String className)
    {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            String err = "Lookup of `{0}` failed. Is the @PaniniJ runtime on the classpath?";
            throw new RuntimeException(format(err, className), ex);
        }
    }


    @SuppressWarnings("unchecked")
    private static Class<Panini$Capsule> lookupPaniniCapsuleClass() {
        return (Class<Panini$Capsule>) lookupRuntimeClass("org.paninij.runtime.Panini$Capsule");
    }


    @SuppressWarnings("unchecked")
    private static Class<CapsuleInterface> lookupCapsuleInterfaceClass() {
        return (Class<CapsuleInterface>) lookupRuntimeClass("org.paninij.lang.CapsuleInterface");
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