package org.paninij.runtime.check;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import me.dwtj.objectgraph.Explorer;
import me.dwtj.objectgraph.GreedyNavigator;
import me.dwtj.objectgraph.Navigator;

import org.paninij.runtime.util.IdentitySet;
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
         * calls to `isSafeTransfer()`.
         */
        private final static IdentitySetStore msg_store = new IdentitySetStore();


        /**
         * Thread-local storage used to explore the object graph of a capsule's local state. It is
         * used across all calls to `isSafeTransfer()`.
         */
        private final static IdentitySetStore local_store = new IdentitySetStore();


        /**
         * Thread-local storage used to as a temporary in the exploration of an object graph. It is
         * used across all calls to `findUnsafeFrom()`.
         */
        //private final static IdentitySetStore safe_store = new IdentitySetStore();
        private final static IdentitySetStore worklist_store = new IdentitySetStore();


        /**
         * Returns true if it is safe to transfer ownership of the outgoing `msg` (and its object
         * graph) from a capsule whose state is fully encapsulated within `local`.
         */
        public static boolean isSafeTransfer(Object msg, Object local)
        {
            return areDisjoint(findUnsafe(msg, msg_store),
                               findUnsafe(local, local_store));
        }
        
        
        /**
         * Explores the object graph reachable from `root_obj` in order to find the set of all
         * objects which would be unsafe to transfer from one capsule to another. In this context,
         * we say that an object is "unsafe to transfer", if it would be unsafe have an alias to
         * that object in two different capsules (or threads, for that matter).
         * 
         *  - A `java.util.ArrayList` is never safe to transfer: each is mutable.
         *  - A `String` is always safe to transfer: each is effectively immutable.
         *  
         *  Note that the return value will be the same as the object pointed to by given
         *  `unsafe_store`. Also note that calling this function will clear and fill the objects
         *  pointed to by `worklist`, `safe`, and the given `unsafe_store`.
         *  
         *  Some examples of "safe" and "unsafe" objects.
         *  
         *  - A `String` is safe.
         *  - A `String[]` is unsafe.
         *  - A `String[][]` is unsafe.
         *  - A `Point` (with mutable fields) is unsafe.
         *  - A `Point` (with immutable fields) is safe.
         */
        private static Set<Object> findUnsafe(Object root_obj, IdentitySetStore unsafe_store)
        {
            // Invariant: `worklist` only contains objects which have been discovered and found to
            // be `unsafe` (i.e. anything in `worklist` is already in `unsafe` objects).

            IdentitySet unsafe = unsafe_store.get();      // The set of unsafe discovered objects.
            //IdentitySet safe = safe_store.get();          // The set of safe discovered objects.
            IdentitySet worklist = worklist_store.get();  // The set of objects yet to be explored.

            unsafe.clear();
            //safe.clear();
            worklist.clear();
            
            if (isSafeRoot(root_obj) == false)
            {
                unsafe.add(root_obj);
                worklist.add(root_obj);
            }
            
            Object obj;
            while ((obj = worklist.remove()) != null)
            {
                Class<? extends Object> cls = obj.getClass();

                if (cls.isArray()) {
                    findUnsafe$addComponents(obj, cls, unsafe, worklist);
                } else {
                    findUnsafe$addFields(obj, cls, unsafe, worklist);
                }
            }

            return unsafe;
        }
            

        /**
         * A helper method just for `findUnsafe()` for adding unsafe components of an array `obj`.
         */
        private static void findUnsafe$addComponents(Object obj, Class<? extends Object> cls,
                                                     IdentitySet unsafe, IdentitySet worklist)
        {
            if (obj instanceof Object[] && isSafe(cls.getComponentType()) == false)
            {
                for (Object found : (Object[]) obj) {
                    if (found != null && unsafe.add(found) == true) {
                        worklist.add(found);
                    }
                }
            }            
        }
        

        /**
         * A helper method just for `findUnsafe()` for adding unsafe fields of an object.
         */
        private static void findUnsafe$addFields(Object obj, Class<? extends Object> cls,
                                                 IdentitySet unsafe, IdentitySet worklist)
        {
            for (Field f : cls.getFields())
            {
                Object found = getFieldValueIfUnsafe(obj, f);
                if (found != null && unsafe.add(found) == true) {
                    worklist.add(found);
                }
            } 
        }
        
        
        private static boolean isSafeRoot(Object obj)
        {
            // TODO: I suspect that the semantics of checking "safe" on a root object is slightly
            // different than when we are in the `while` loop. So, be conservative and assume always
            // `false` for now.
            return isSafe(obj.getClass());
        }
        
        
        /**
         * Preconditions:
         * 
         *  - `obj` is classified as unsafe.
         *  - `f` is a field of `obj.getClass()`.
         * 
         * @return The stored value of the 
         */
        private static Object getFieldValueIfUnsafe(Object obj, Field f)
        {
            if (isSafe(f.getDeclaringClass()))
            {
                return null;
            }
            else
            {
                try {
                    f.setAccessible(true);
                    return (f.get(obj));
                }
                catch (IllegalAccessException ex) { return null; }
            }
        }
        
       
        private static boolean isSafe(Class<? extends Object> cls)
        {
            return cls.isPrimitive()
                // Known safe java classes (including the eight primitive wrapper types).
                || cls == String.class
                || cls == Integer.class
                || cls == Boolean.class
                || cls == Byte.class
                || cls == Character.class
                || cls == Double.class
                || cls == Short.class
                || cls == Long.class
                || cls == Float.class

                // Known safe panini classes.
                || cls == org.paninij.lang.String.class;

                // TODO: Void?
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
