package org.paninij.runtime.check;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.paninij.lang.Capsule;
import org.paninij.runtime.Panini$System;
import org.paninij.runtime.util.IdentitySet;
import org.paninij.runtime.util.IdentitySetStore;
import org.paninij.runtime.util.IdentityStack;
import org.paninij.runtime.util.IdentityStackStore;


public class DynamicOwnershipTransfer
{
    public static final String ARGUMENT_KEY = "panini.ownershipTransfer.dynamic";
    
    /**
     * @param msg The outgoing message (i.e. transfer) to check against the capsule's state.
     * 
     * Note that this currently uses the `REFLECTION_OPTIMIZED` method. Also note that the capsule's
     * state is retrieved via `Panini$System.self`, so this will not work for capsule execution
     * profiles other than "Thread".
     */
    public static void assertSafeTransfer(Object msg)
    {
        String err = "Capsule performed an illegal ownership transfer: " + Panini$System.self.get();
        //assert REFLECTION_OPTIMIZED.isSafeTransfer(msg, Panini$System.self.get().panini$getAllState()) : err;
        if (REFLECTION.isSafeTransfer(msg, Panini$System.self.get().panini$getAllState()) == false)
        {
            System.err.println(err);
            throw new AssertionError(err);
        }
    }
    
    public static boolean isSafeTransfer(Object msg, Object local, Kind method)
    {
        switch (method) {
        case NONE:
            return true;
        case REFLECTION:
            return REFLECTION.isSafeTransfer(msg, local);
        case NATIVE:
            return NATIVE.isSafeTransfer(msg, local);
        default:
            throw new IllegalArgumentException("Unknown `OwnershipCheckMethod`: " + method);
        }
    }
    
    public static enum Kind
    {
        NONE,
        REFLECTION,
        NATIVE;
        
        /**
         * Converts the given string `s` to the matching enum value. Note that if either `null` or
         * the empty string are interpreted given, then the default `Kind` is returned.
         * 
         * @throws IllegalArgumentException If there is no enum value matching the given string.
         */
        public static Kind fromString(String s)
        {
            if (s == null || s.isEmpty())
                return getDefault();
            if (s.equals("NONE"))
                return NONE;
            if (s.equals("RUNTIME_REFLECTION_OPTIMIZED"))
                return REFLECTION;
            if (s.equals("RUNTIME_NATIVE"))
                return NATIVE;
            
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
        
        public static Kind getDefault() {
            return NONE;
        }
    }



    public static class NONE
    {
        public static boolean isSafeTransfer(Object msg, Object local)
        {
            return true;
        }
    }
    


    public static class REFLECTION
    {
        /**
         * Thread-local storage used to explore the object graph of messages. It is used across all
         * calls to `isSafeTransfer()`.
         */
        private final static IdentitySetStore<Object> msg_store = new IdentitySetStore<Object>();


        /**
         * Thread-local storage used to explore the object graph of a capsule's local state. It is
         * used across all calls to `isSafeTransfer()`.
         */
        private final static IdentitySetStore<Object> local_store = new IdentitySetStore<Object>();


        /**
         * Thread-local storage used to as a temporary in the exploration of an object graph. It is
         * used across all calls to `findUnsafeFrom()`.
         */
        private final static IdentityStackStore<Object> workstack_store = new IdentityStackStore<Object>();


        /**
         * Returns true if it is safe to transfer ownership of the outgoing `msg` (and its object
         * graph) from a capsule whose state is fully encapsulated within `local`.
         */
        public static boolean isSafeTransfer(Object msg, Object local)
        {
            return areDisjoint(findUnsafe(msg, msg_store), findUnsafe(local, local_store));
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
        private static IdentitySet<Object> findUnsafe(Object root_obj, IdentitySetStore<Object> unsafe_store)
        {
            // Invariant: `worklist` only contains objects which have been discovered and found to
            // be `unsafe` (i.e. anything in `worklist` is already in `unsafe` objects).

            // The set of unsafe discovered objects.
            IdentitySet<Object> unsafe = unsafe_store.get();

            // The set of objects yet to be explored.
            IdentityStack<Object> workstack = workstack_store.get();

            unsafe.clear();
            workstack.clear();
            
            if (isSafeRoot(root_obj) == false)
            {
                unsafe.add(root_obj);
                workstack.add(root_obj);
            }
            
            Object obj;
            while ((obj = workstack.pop()) != null)
            {
                Class<? extends Object> cls = obj.getClass();
                assert isAlwaysUnsafe(cls) == false:
                    "An object of class " + cls + " is always unsafe to transfer.";

                if (cls.isArray()) {
                    findUnsafe$addComponents(obj, cls, unsafe, workstack);
                } else {
                    findUnsafe$addFields(obj, cls, unsafe, workstack);
                }
            }

            return unsafe;
        }
            

        /**
         * A helper method just for `findUnsafe()` for adding unsafe components of an array `obj`.
         */
        private static void findUnsafe$addComponents(Object obj, Class<? extends Object> cls,
                                                     IdentitySet<Object> unsafe,
                                                     IdentityStack<Object> workstack)
        {
            if (obj instanceof Object[] && isAlwaysSafe(cls.getComponentType()) == false)
            {
                for (Object found : (Object[]) obj) {
                    if (found != null && unsafe.add(found) == true) {
                        workstack.push(found);
                    }
                }
            }            
        }
        

        /**
         * A helper method just for `findUnsafe()` for adding unsafe fields of an object.
         */
        private static void findUnsafe$addFields(Object obj, Class<? extends Object> cls,
                                                 IdentitySet<Object> unsafe,
                                                 IdentityStack<Object> workstack)
        {
            for (Field f : findUnsafe$getAllFields(cls))
            {
                Object found = getFieldValueIfUnsafe(obj, f);
                if (found != null && unsafe.add(found) == true) {
                    workstack.add(found);
                }
            } 
        }
        
        
        /**
         * A helper method just for `findUnsafe()` for getting all of the fields from a class.
         */
        private static List<Field> findUnsafe$getAllFields(Class<? extends Object> cls) {
        	//1.7 compliant
        	List<Field> fields = new ArrayList<Field>();
        	for(Field f : cls.getFields())
        	{
        		fields.add(f);
        	}
        	for(Field f : cls.getDeclaredFields())
        	{
        		fields.add(f);
        	}
        	
        	return fields;
        	//1.8 feature
        	/*
            return Stream.concat(Arrays.stream(cls.getFields()),
                                 Arrays.stream(cls.getDeclaredFields())).collect(Collectors.toList());
        	*/
        }
        
        
        private static boolean isSafeRoot(Object obj)
        {
            // TODO: I suspect that the semantics of checking "safe" on a root object is slightly
            // different than when we are in the `while` loop. So, be conservative and assume always
            // `false` for now.
            return isAlwaysSafe(obj.getClass());
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
            if (isAlwaysSafe(f.getDeclaringClass()))
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
        
       
        private static boolean isAlwaysSafe(Class<? extends Object> cls)
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
        
        
        private static boolean isAlwaysUnsafe(Class<? extends Object> cls)
        {
            Capsule anno = (Capsule) cls.getAnnotation(Capsule.class);
            return anno != null;
        }
        

        private static boolean areDisjoint(IdentitySet<Object> msg_refs,
                                           IdentitySet<Object> local_refs)
        {
            for (Object obj : msg_refs) {
                if (local_refs.contains(obj)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    
    public static class NATIVE
    {
        public static boolean isSafeTransfer(Object msg, Object local)
        {
            String err = "The `NATIVE` dynamic ownership transfer check has not been implemented.";
            throw new UnsupportedOperationException(err);
        }
    }
}
