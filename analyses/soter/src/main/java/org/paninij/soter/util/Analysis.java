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
package org.paninij.soter.util;

public abstract class Analysis
{
    protected boolean hasBeenPerformed = false;

    /**
     * If this method has never been called before, then any sub-analyses and the main analysis are
     * performed. If this method has been previously called, then it will return immediately.
     * 
     * Warning: implementers of `Analysis` should not not generally override `perform()`. They are
     * expected to usually override `performAnalysis()` and `performSubAnalyses()`.
     */
    public void perform()
    {
        if (hasBeenPerformed) {
            return;
        }
        performSubAnalyses();
        performAnalysis();
        hasBeenPerformed = true;
    }

     /**
     * Calling this performs the main analysis which an analysis class provides. After this is
     * called, the major results should have been generated. Note that implementations of this
     * method are not expected to be idempotent, because the `perform()` wrapper is provided to
     * provide idempotency checking.
     */
    protected abstract void performAnalysis();

    /**
     * If the main analysis depends any sub-analyses being performed before it can be performed,
     * then those calls should happen here.
     */
    protected void performSubAnalyses()
    {
        // By default, assume that there are no sub-analyses, and do nothing.
    }

}
