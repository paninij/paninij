package org.paninij.soter.util;

import java.util.Map;
import java.util.Set;

import org.paninij.runtime.util.IdentitySet;

public class Sets
{
    private Sets() {
        // Cannot instantiate class.
    }
    
    public static <T> boolean isWellDefinedOverDomain(Map<T, ?> map, IdentitySet<T> domain)
    {
         // Check that the domain `map` is equivalent to `domain`.
        Set<T> keySet = map.keySet();
        if (keySet.equals(domain)) {
            return false;
        }
        
        // Check that every element of the range of `map` is non-null.
        for (T key: keySet) {
            if (map.get(key) == null) {
                return false;
            }
        }
        
        return true;
    }
    
    public static <T> boolean isWellDefinedOverDomain(Map<T, ?> map, Set<T> domain)
    {
         // Check that the domain `map` is equivalent to `domain`.
        Set<T> keySet = map.keySet();
        if (keySet.equals(domain)) {
            return false;
        }
        
        // Check that every element of the range of `map` is non-null.
        for (T key: keySet) {
            if (map.get(key) == null) {
                return false;
            }
        }
        
        return true;
    }
}
