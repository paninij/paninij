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
 * Contributor(s): Dalton Mills, David Johnston, Kristin Clemens, Trey Erenberger
 */
package org.paninij.lang;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>
 * Used to designate a java class as a template for a capsule.
 * <h3>Purpose</h3>
 * The purpose of this annotation is to designate a java class to act as a template for a capsule.
 * 
 * <h3>Details</h3>
 * <p>
 * When a class is annotated with &#64;Capsule, it will get processed and capsule artifacts will be generated based on the procedures 
 * and state of the java class. The name of the capsule produced will be the class name of the annotated class with Template removed. 
 * (e.g. HelloWorldTemplate produces HelloWorld capsule) A capsule is subject to a number of restrictions based on the Panini model.
 * <h3>Exceptions</h3>
 * <p>
 * A class annotated with &#64;Capsule must have Template at the end of the class name. (e.g. HelloWorldTemplate)
 * <h3>Examples</h3>
 * <p>
 * In this example, a basic capsule is described by a java class. This will generate capsule artifacts which can be run as a capsule system.
 * <blockquote><pre>
 * &#64;Capsule
 * public class HelloWorldShortTemplate {
 *     
 *     void run() {
 *         System.out.println("Hello World!");
 *     }
 * }
 * </pre></blockquote>
 * <h3>Internal Notes</h3>
 * <p>
 * None
 * <h3>Associated Annotations</h3>
 * <p>
 * This annotation and {@link org.paninij.lang.Signature &#64;Signature} are used to set up the class types used in a capsule system.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Capsule {}