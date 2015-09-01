package org.paninij.apt.check;

public class StaticOwnershipTransfer
{
    public static final String ARGUMENT_KEY = "panini.ownershipTransfer.static";
    
    public enum Kind
    {
        NONE,
        SOTER;
    
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
            if (s.equals("SOTER"))
                return SOTER;

            throw new IllegalArgumentException("Not a known `StaticOwnershipTransfer.Kind`: " + s);
        }
        
        public static boolean isKnown(String s)
        {
            try {
                fromString(s);
                return true;
            }
            catch (IllegalArgumentException ex) {
                return false;
            }
        }
        
        public static Kind getDefault() {
            return NONE;
        }
    }
}
