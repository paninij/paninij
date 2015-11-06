package org.paninij.lang;

import java.lang.String;  // Needed to prevent unintended use of `org.paninij.lang.String`.
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.paninij.runtime.Panini$Capsule;

public class CapsuleSystem
{
    public final static ExecutionProfile DEFAULT_EXECUTION_PROFILE = ExecutionProfile.THREAD;


    /**
     * Starts a capsule system with the default execution profile. The 0th arg is interpreted to be
     * the root capsule from which the system will be started.
     * 
     * @param args     The root capsule's name followed by the arguments to be passed into the root
     *                 capsule's `main()` method.
     */
    public static void main(String[] args)
    {
        if (args.length == 0) {
            String err = "Must give a fully qualified capsule name as the first argument.";
            throw new IllegalArgumentException(err);
        }
        
        start(args[0], Arrays.copyOfRange(args, 1, args.length));
    }
    

    /**
     * Starts a capsule system from the given root capsule using the default execution profile.
     * 
     * @param root  The fully-qualified name of a capsule which will act as the root capsule.
     * @param args  The arguments to be passed into the root capsule's `main()` method.
     */
    public static void start(String root, String[] args)
    {
        start(root, DEFAULT_EXECUTION_PROFILE, args);
    }
    
    public static void start(Class<? extends Panini$Capsule> root, String[] args)
    {
        start(root, DEFAULT_EXECUTION_PROFILE, args);
    }

    
    /**
     * Starts a capsule system from the given root capsule using the given execution profile.
     * 
     * @param root     The fully-qualified name of a capsule which will act as the root capsule.
     * @param profile  The execution profile with which the capsule system will run.
     * @param args     The arguments to be passed into the root capsule's `main()` method.
     */
    public static void start(String root, ExecutionProfile profile, String[] args)
    {
        try {
            CapsuleFactory capsuleFactory = new CapsuleFactory(root);
            Class<? extends Panini$Capsule> clazz = capsuleFactory.getInstantiableClass(profile);
            Method main = clazz.getDeclaredMethod("main", String[].class);
            main.invoke(null, (Object) args);
        }
        catch (ClassNotFoundException | NoSuchMethodException    | SecurityException |
               IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    public static void start(Class<? extends Panini$Capsule> root, ExecutionProfile profile, String[] args)
    {
        try {
            CapsuleFactory capsuleFactory = new CapsuleFactory(root);
            Class<? extends Panini$Capsule> clazz = capsuleFactory.getInstantiableClass(profile);
            Method main = clazz.getDeclaredMethod("main", String[].class);
            main.invoke(null, (Object) args);
        }
        catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
               IllegalAccessException  ex) {
            throw new RuntimeException(ex);
        }
    }
}
