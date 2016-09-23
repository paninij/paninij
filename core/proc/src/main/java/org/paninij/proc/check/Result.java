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
package org.paninij.proc.check;

import javax.lang.model.element.Element;

public interface Result
{
    boolean ok();
    String err();
    Class<? extends Check> source();
    
    /**
     * May return null.
     */
    Element offender();
    
    public static Result ok = new Result()
    {
        public boolean ok() {
            return true;
        }
        public String err() {
            return null;
        }
        public Class<? extends Check> source() {
            return null;
        }
        public Element offender() {
            return null;
        }
    };
    
    public static class Error implements Result
    {
        private final String err;
        private final Class<? extends Check> source;
        private final Element offender;

        /**
         * @throws  IllegalArgumentException  If `err` or `source` is null.
         */
        public Error(String err, Class<? extends Check> source, Element offender) {
            if (err == null || source == null) {
                throw new IllegalArgumentException();
            }
            this.err = err;
            this.source = source;
            this.offender = offender;
        }
        
        @Override
        public boolean ok() {
            return false;
        }
        
        @Override
        public String err() {
            return err;
        }

        @Override
        public Class<? extends Check> source() {
            return source;
        }
        
        @Override
        public Element offender() {
            return offender;
        }
    }
}
