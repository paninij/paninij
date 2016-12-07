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
 *  Dr. Hridesh Rajan,
 *  Dalton Mills,
 *  David Johnston,
 *  Trey Erenberger
 *  Jackson Maddox
 *******************************************************************************/

package org.paninij.runtime;

import org.paninij.lang.EventExecution;

public class EventMessage<T> implements Panini$Message
{
    public final int procID;
    public final EventExecution<T> ex;
    public final T arg0;

    public EventMessage(int procID, EventExecution<T> ex, T arg0) {
        this.procID = procID;
        this.ex = ex;
        this.arg0 = arg0;
    }

    @Override
    public int panini$msgID() {
        return procID;
    }
}
