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

package org.paninij.runtime;

import java.util.concurrent.TimeUnit;

public abstract class Capsule$Serial implements Panini$Capsule
{

    protected int panini$links;
    protected final Panini$ErrorQueue panini$errors;
    protected boolean panini$terminated;

    protected Capsule$Serial() {
        panini$links = 0;
        panini$errors = new Panini$ErrorQueue();
        panini$terminated = false;
    }

    @Override
    public void panini$start()
    {
        this.run();
    }

    @Override
    public void panini$push(Object o)
    {
        // Do nothing. Serial capsules do not have a queue

    }

    @Override
    public void panini$join() throws InterruptedException
    {
        // TODO serial capsules do not have a thread

    }

    @Override
    public void panini$openLink()
    {
        panini$links++;
    }

    @Override
    public void panini$closeLink()
    {
        panini$links--;
        if (panini$links == 0 && !panini$terminated) panini$onTerminate();
    }

    /**
     * Initialize the locals of this capsule.
     *
     * Must (in general) be called *before* `panini$initState()`.
     */
    protected void panini$initLocals() {
        // Do nothing.
    }

    /**
     * Send a PANINI$CLOSE_LINK message to all reference capsules
     */
    protected void panini$onTerminate() {
        // Do nothing
    }

    protected void panini$checkRequiredFields() {
        // Do nothing
    }

    @Override
    public void exit()
    {
        // TODO ??
        this.panini$closeLink();
    }

    @Override
    public void yield(long millis)
    {
        if (millis < 0) {
            throw new IllegalArgumentException();
        }

        try {
            Thread.sleep(millis);
            // TODO: this may also be a good place to introduce interleaving.
        } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO: What should be the semantics here?
        }
    }

    public void run() {
        // Do nothing
    }

    protected void panini$initState()
    {
        // TODO Auto-generated method stub

    }

    public Object panini$getAllState()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Throwable panini$pollErrors() {
        return panini$errors.poll();
    }

    public Throwable panini$pollErrors(long timeout, TimeUnit unit)
    {
        try {
            return panini$errors.poll(timeout, unit);
        } catch (InterruptedException ex) {
            return null;
        }
    }
}
