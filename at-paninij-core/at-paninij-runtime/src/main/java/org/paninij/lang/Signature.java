
package org.paninij.lang;
/**
 * <p>
 * Used to designate a java interface as a template for a signature.
 * <h3>Purpose</h3>
 * The purpose of this annotation is to designate a java interface to act as a template for a signature.
 * 
 * <h3>Details</h3>
 * <p>
 * Signatures are used to allow capsules to have common interfaces which makes it easier build modular systems. A signature acts 
 * in the same manner as a java interface. Capsule templates that implement a signature implement the signature template, but the 
 * artifacts that are generated as a result will use the generated types. (e.g. ConsoleTemplate implements StreamTemplate, Console 
 * implements Stream)
 * <h3>Exceptions</h3>
 * <p>
 * An interface annotated with @Signature must have Template at the end of the class name. (e.g. HelloWorldTemplate)
 * <p>
 * An interface annotated with @Signature may not contain default methods.
 * <h3>Examples</h3>
 * <p>
 * In this example, a basic signature is defined by a java interface. We also define a capsule that implements this signature and as a result 
 * can be referred to using this interface type.
 * <blockquote><pre>
 * &#64;Signature
 * public interface StreamTemplate {
 *     
 *     public void write(String s);
 * }
 * </pre></blockquote>
 * <blockquote><pre>
 * &#64;Capsule
 * public class ConsoleTemplate implements StreamTemplate {
 *     
 *     void write(String s) {
 *         System.out.println(s);
 *     }
 * }
 * </pre></blockquote>
 * <h3>Internal Notes</h3>
 * <p>
 * None
 * <h3>Associated Annotations</h3>
 * <p>
 * This annotation and {@link org.paninij.lang.Capsule @Capsule} are used to set up the class types used in a capsule system.
 **/
public @interface Signature { }