/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills, David Johnston, Trey Erenberger
 */
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
     * Initialize the children of this capsule.
     *
     * Must (in general) be called *before* `panini$initState()`.
     */
    protected void panini$initChildren() {
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
