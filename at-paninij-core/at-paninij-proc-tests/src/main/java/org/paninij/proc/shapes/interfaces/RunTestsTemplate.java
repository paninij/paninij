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
package org.paninij.proc.shapes.interfaces;

import java.util.concurrent.Future;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule
public class RunTestsTemplate
{
    @Local Tests test;

    public void run() {
        MyInterface t = test.getThing();
        MyInterface b = test.blockGetThing();
        Future<MyInterface> f = test.futureGetThing();

        MyInterface thing;
        try
        {
            thing = f.get();
            System.out.println(thing.getTheNumber());
            System.out.println(thing.getTheObject());
            System.out.println(thing.getTheString());
        }
        catch (Throwable ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
