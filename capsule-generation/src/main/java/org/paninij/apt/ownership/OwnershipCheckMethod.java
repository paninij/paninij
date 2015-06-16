package org.paninij.apt.ownership;

public enum OwnershipCheckMethod
{
    RUNTIME_REFLECTION_NAIVE,
    RUNTIME_REFLECTION_OPTIMIZED;
    
    /**
     * Converts the given string `s` to the matching enum value.
     * 
     * @throws IllegalArgumentException If there is no enum value matching the given string.
     */
    public static OwnershipCheckMethod fromString(String s)
    {
        if (s == null) {
            throw new IllegalArgumentException("Not a known `OwnershipCheckMethod`: <null>");
        }
        
        if (s.equals("RUNTIME_RELFECTION_NAIVE"))
            return RUNTIME_REFLECTION_NAIVE;
        if (s.equals("RUNTIME_REFLECTION_OPTIMIZED"))
            return RUNTIME_REFLECTION_OPTIMIZED;
        
        throw new IllegalArgumentException("Not a known `OwnershipCheckMethod`: " + s);
    }
    
    public static OwnershipCheckMethod getDefault() {
        return RUNTIME_REFLECTION_NAIVE;
    }
    
    public static String getArgumentKey() {
        return "ownership.check.method";
    }
}
