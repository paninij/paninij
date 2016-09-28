/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
package org.paninij.proc.check.ownership;

@Deprecated
public class CheckForStaticOwnershipTransfer
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

            throw new IllegalArgumentException("Not a known `CheckForStaticOwnershipTransfer.Kind`: " + s);
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
