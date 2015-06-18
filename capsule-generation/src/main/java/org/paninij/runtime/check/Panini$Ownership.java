package org.paninij.runtime.check;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import me.dwtj.objectgraph.Explorer;
import me.dwtj.objectgraph.GreedyNavigator;
import me.dwtj.objectgraph.Navigator;

import org.paninij.runtime.util.IdentitySetStore;


public class Panini$Ownership
{
    public static boolean isSafeTransfer(Object msg, Object local, CheckMethod method)
    {
        switch (method) {
        case RUNTIME_REFLECTION_NAIVE:
            return RUNTIME_REFLECTION_NAIVE.isSafeTransfer(msg, local);
        case RUNTIME_REFLECTION_OPTIMIZED:
            return RUNTIME_REFLECTION_OPTIMIZED.isSafeTransfer(msg, local);
        case RUNTIME_NATIVE:
            return RUNTIME_NATIVE.isSafeTransfer(msg, local);
        default:
            throw new IllegalArgumentException("Unknown `OwnershipCheckMethod`: " + method);
        }
    }
    
    
    
    public static enum CheckMethod
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



    public static class RUNTIME_REFLECTION_NAIVE
    {
        public static boolean isSafeTransfer(Object msg, Object local)
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
    }

    
    
    public static class RUNTIME_REFLECTION_OPTIMIZED
    {
        /**
         * Thread-local storage used to explore the object graph of messages. It is used across all
         * calls to `isSafeTransfer$RUNTIME_REFLECTION_OPTIMIZED()`.
         */
        private final static IdentitySetStore msg_store = new IdentitySetStore();


        /**
         * Thread-local storage used to explore the object graph of a capsule's local state. It is
         * used across all calls to `isSafeTransfer$RUNTIME_REFLECTION_OPTIMIZED()`.
         */
        private final static IdentitySetStore local_store = new IdentitySetStore();


        /**
         * Returns true if it is safe to transfer ownership of the outgoing `msg` (and its object graph)
         * from a capsule whose state is fully encapsulated within `local`
         */
        public static boolean isSafeTransfer(Object msg, Object local)
        {
            return areDisjoint(findUnsafeFrom(msg, msg_store.get()),
                               findUnsafeFrom(local, local_store.get()));
        }
        
        
        /**
         * Explores the object graph reachable from `obj` in order to find the set of all objects which
         * would be unsafe to transfer from one capsule to another. By unsafe to transfer, we mean that
         * they would be unsafe to transfer if an alias to that object that was retained in the
         * original capsule. For example,
         * 
         *  - A `java.util.ArrayList` is never safe to transfer: they are mutable.
         *  - A `String` is always safe to transfer: they are effectively immutable.
         *  
         *  Note that the return value will be the same object as the given `store`.
         */
        private static Set<Object> findUnsafeFrom(Object obj, Set<Object> store)
        {
            throw new UnsupportedOperationException();
            //return store;
        }
        
        
        private static boolean isUnsafe(Object obj)
        {
            throw new UnsupportedOperationException();
        }
        

        private static boolean areDisjoint(Set<Object> fst, Set<Object> snd)
        {
            for (Object obj : fst) {
                if (snd.contains(obj)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    
    public static class RUNTIME_NATIVE
    {
        public static boolean isSafeTransfer(Object msg, Object local)
        {
            // TODO: Everything!
            return false;
        }
    }
}
