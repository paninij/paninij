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
 * Contributor(s): Dalton Mills
 */
package org.paninij.apt.model;

/*
 * All enumerated message behaviors
 * See: https://github.com/hridesh/panini/wiki/Enumerating-Consequences-of-a-Procedure's-Properties-Along-Three-Dimensions
 */
public enum Behavior
{
    UNBLOCKED_SIMPLE,
    BLOCKED_FUTURE,
    UNBLOCKED_FUTURE,
    UNBLOCKED_DUCK,
    UNBLOCKED_PREMADE,
    BLOCKED_PREMADE,
    ERROR;

    /*
     * A 3d lookup table of all behaviors based on duckability,
     * category of the return type, and the annotations present
     */
    public static Behavior[][][] table = new Behavior[][][] {
        // unduckables
        {
            // normal
            {Behavior.BLOCKED_FUTURE, Behavior.UNBLOCKED_FUTURE, Behavior.BLOCKED_FUTURE, Behavior.ERROR},

            // void
            {Behavior.UNBLOCKED_SIMPLE, Behavior.UNBLOCKED_FUTURE, Behavior.BLOCKED_FUTURE, Behavior.ERROR},

            // primitive
            {Behavior.BLOCKED_FUTURE, Behavior.UNBLOCKED_FUTURE, Behavior.BLOCKED_FUTURE, Behavior.ERROR},
        },

        // duckables
        {
            // normal
            {Behavior.UNBLOCKED_DUCK, Behavior.UNBLOCKED_FUTURE, Behavior.BLOCKED_FUTURE, Behavior.UNBLOCKED_DUCK},

            // void
            {Behavior.UNBLOCKED_SIMPLE, Behavior.UNBLOCKED_FUTURE, Behavior.BLOCKED_FUTURE, Behavior.ERROR},

            // primitive
            {Behavior.ERROR, Behavior.ERROR, Behavior.ERROR, Behavior.ERROR},
        },

        // ducked
        {
            // normal
            {Behavior.UNBLOCKED_PREMADE, Behavior.ERROR, Behavior.BLOCKED_PREMADE, Behavior.UNBLOCKED_PREMADE},

            // void
            {Behavior.ERROR, Behavior.ERROR, Behavior.ERROR, Behavior.ERROR},

            // primitive
            {Behavior.ERROR, Behavior.ERROR, Behavior.ERROR, Behavior.ERROR},
        }
    };

    public static Behavior determine(Procedure procedure) {
        int duckable = 0;
        int category = 0;
        int annotation = 0;

        Type returnType = procedure.getReturnType();

        switch (returnType.getDuckability()) {
        case UNDUCKABLE:
            duckable = 0;
            break;
        case DUCKABLE:
            duckable = 1;
            break;
        case DUCKED:
            duckable = 2;
            break;
        default:
            return Behavior.ERROR;
        }

        switch (returnType.getCategory()) {
        case NORMAL:
            category = 0;
            break;
        case VOID:
            category = 1;
            break;
        case PRIMITIVE:
            category = 2;
            break;
        default:
            return Behavior.ERROR;
        }

        switch (procedure.getAnnotationKind()) {
        case NONE:
            annotation = 0;
            break;
        case FUTURE:
            annotation = 1;
            break;
        case BLOCK:
            annotation = 2;
            break;
        case DUCKFUTURE:
            annotation = 3;
            break;
        default:
            return Behavior.ERROR;
        }

        return Behavior.table[duckable][category][annotation];
    }
}
