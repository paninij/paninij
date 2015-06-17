package org.paninij.runtime.check;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import me.dwtj.objectgraph.Explorer;
import me.dwtj.objectgraph.GreedyNavigator;
import me.dwtj.objectgraph.Navigator;

public class Panini$Ownership
{
    /**
     * Returns true if it is safe to transfer ownership of the outgoing `msg` (and its object graph)
     * from a capsule whose state is fully encapsulated within `local`
     */
    public static boolean isSafeTransfer(Object msg, Object local)
    {
        return isSafeTransfer(msg, local, CheckMethod.getDefault());
    }
    
    
    public static boolean isSafeTransfer(Object msg, Object local, CheckMethod method)
    {
        switch (method) {
        case RUNTIME_REFLECTION_NAIVE:
            return isSafeTransfer$RUNTIME_REFLECTION_NAIVE(msg, local);
        case RUNTIME_REFLECTION_OPTIMIZED:
            return isSafeTransfer$RUNTIME_REFLECTION_OPTIMIZED(msg, local);
        case RUNTIME_NATIVE:
            return isSafeTransfer$RUNTIME_NATIVE(msg, local);
        default:
            throw new IllegalArgumentException("Unknown `OwnershipCheckMethod`: " + method);
        }
    }
    
    
    public static boolean isSafeTransfer$RUNTIME_REFLECTION_NAIVE(Object msg, Object local)
    {
        // These predicates and the navigator are all stateless, so they are safe to reuse.
        final Predicate<Object> nav_from = (obj -> obj instanceof org.paninij.lang.String == false
                                                && obj instanceof java.lang.String == false);
        final Predicate<Class<?>> nav_to = (clazz -> clazz != org.paninij.lang.String.class
                                                  && clazz != java.lang.String.class);

        final Navigator navigator = new GreedyNavigator(nav_from, nav_to);
        
        Explorer local_explorer = new Explorer(navigator);
        local_explorer.explore(local);
        
        Explorer msg_explorer = new Explorer(navigator);
        msg_explorer.explore(msg);
        
        // Filter out those which are known to be safe to transfer.
        List<Object> local_refs = local_explorer.visited.identities.keySet().stream()
                                     .collect(Collectors.toList());

        List<Object> msg_refs = msg_explorer.visited.identities.keySet().stream()
                                   .collect(Collectors.toList());

        // Return true iff the intersection of these two sets is empty.
        msg_refs.retainAll(local_refs);
        return msg_refs.isEmpty();
    }


    public static boolean isSafeTransfer$RUNTIME_REFLECTION_OPTIMIZED(Object msg, Object local)
    {
        // TODO: Everything!
        return false;
    }
    
    
    public static boolean isSafeTransfer$RUNTIME_NATIVE(Object msg, Object local)
    {
        // TODO: Everything!
        return false;
    }
    
    
    public enum CheckMethod
    {
        RUNTIME_REFLECTION_NAIVE,
        RUNTIME_REFLECTION_OPTIMIZED,
        RUNTIME_NATIVE;
        
        /**
         * Converts the given string `s` to the matching enum value.
         * 
         * @throws IllegalArgumentException If there is no enum value matching the given string.
         */
        public static CheckMethod fromString(String s)
        {
            if (s == null) {
                throw new IllegalArgumentException("Not a known `OwnershipCheckMethod`: <null>");
            }
            
            if (s.equals("RUNTIME_REFLECTION_NAIVE"))
                return RUNTIME_REFLECTION_NAIVE;
            if (s.equals("RUNTIME_REFLECTION_OPTIMIZED"))
                return RUNTIME_REFLECTION_OPTIMIZED;
            if (s.equals("RUNTIME_NATIVE"))
                return RUNTIME_NATIVE;
            
            throw new IllegalArgumentException("Not a known `OwnershipCheckMethod`: " + s);
        }
        
        public static boolean isKnown(String s)
        {
            try
            {
                fromString(s);
                return true;
            }
            catch (IllegalArgumentException ex)
            {
                return false;
            }
        }
        
        public static CheckMethod getDefault() {
            return RUNTIME_REFLECTION_NAIVE;
        }
        
        public static String getArgumentKey() {
            return "ownership.check.method";
        }
    }
}
