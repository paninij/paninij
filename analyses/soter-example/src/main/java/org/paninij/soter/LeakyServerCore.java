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
package org.paninij.soter;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;

@Capsule
class LeakyServerCore
{
    Secret serverSecret = new Secret();
    
    @Block
    Integer getInteger() {
        return new Integer(9);
    }

    void giveInteger(Integer i) {
        // Nothing to do.
    }
    
    void giveSecret(Secret s) {
        // Nothing to do.
    }
 
    Secret getSecret() {
        return serverSecret;
    }
    
    @Block
    LeakyServerCore getCoreReference() {
        return this;
    }
}
