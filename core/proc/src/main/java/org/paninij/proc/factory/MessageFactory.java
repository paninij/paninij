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

package org.paninij.proc.factory;

import org.paninij.proc.model.Procedure;
import org.paninij.proc.util.MessageShape;
import org.paninij.proc.util.SourceFile;

import java.util.HashSet;


public class MessageFactory implements ArtifactFactory<Procedure>
{
    private HashSet<String> generated;

    private FutureMessageFactory futureMessageFactory;
    private SimpleMessageFactory simpleMessageFactory;
    private DuckMessageFactory duckMessageFactory;

    public MessageFactory() {
        this.generated = new HashSet<>();
        this.futureMessageFactory = new FutureMessageFactory();
        this.simpleMessageFactory = new SimpleMessageFactory();
        this.duckMessageFactory = new DuckMessageFactory();
    }

    public SourceFile make(Procedure procedure) {

        MessageShape shape = new MessageShape(procedure);

        if (this.generated.add(shape.encoded))
        {
            switch (shape.category) {
            case DUCKFUTURE:
                return this.duckMessageFactory.make(procedure);
            case FUTURE:
                return this.futureMessageFactory.make(procedure);
            case PREMADE:
                // premade are already created
                return null;
            case SIMPLE:
                return this.simpleMessageFactory.make(procedure);
            case ERROR:
            default:
                String err = "Procedure has unknown message kind: " + shape.category;
                throw new IllegalArgumentException(err);
            }
        }
        return null;
    }
}
