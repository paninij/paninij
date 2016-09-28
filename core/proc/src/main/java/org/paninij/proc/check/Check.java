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

import org.paninij.proc.util.Source;

import javax.lang.model.element.Element;

public interface Check
{
    interface Result
    {
        boolean ok();

        /** May throw {@link IllegalStateException}. */
        String errMsg();

        /** May throw {@link IllegalStateException}. */
        Class<? extends Check> source();

        /** May throw {@link IllegalStateException}. */
        Element offender();

        Result OK = new Result()
        {
            @Override public boolean ok() {
                return true;
            }
            @Override public String errMsg() {
                throw new IllegalStateException("Check result was OK, so no error message.");
            }
            @Override public Class<? extends Check> source() {
                throw new IllegalStateException("Check result was OK, so no error source.");
            }
            @Override public Element offender() {
                throw new IllegalStateException("Check result was OK, so no offending element.");
            }
            @Override public String toString() {
                return "Result.OK";
            }
        };

        /**
         * @throws  IllegalArgumentException  If `err` or `source` is null.
         */
        static Result error(String errMsg, Class<? extends Check> source, Element offender)
        {
            if (errMsg == null || source == null) {
                throw new IllegalArgumentException();
            }
            return new Result() {
                @Override public boolean ok() {
                    return false;
                }
                @Override public String errMsg() {
                    return errMsg;
                }
                @Override public Class<? extends Check> source() {
                    return source;
                }
                @Override public Element offender() {
                    return offender;
                }
                @Override public String toString() {
                    return Source.format("Result.error(errMsg=#0, source=#1, offender=#2)",
                                         errMsg, source, offender);
                }
            };
        }
    }
}
