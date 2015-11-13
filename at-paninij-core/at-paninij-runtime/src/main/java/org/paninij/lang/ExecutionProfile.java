package org.paninij.lang;
/**
 * <p>Used to dictate capsule-to-thread mappings.
 * 
 * <h3>Mappings</h3>
 * <ol>
 * <li>MOCKUP - Creates capsules with `stub` procedures. Used behind-the-scenes by the annotation 
 * 			processor.</li>
 * <li>THREAD - Each capsule gets it's own JVM thread.</li>
 * <li>TASK - Capsules are assigned to a thread pool in round-robin fashion.</li>
 * <li>MONITOR - Capsules procedures are given basic synchronization.</li>
 * <li>SERIAL - Capsules are sequential (no threads).</li>
 * </ol>
 * 
 * <p>Future works will attempt to automatically assign execution profiles on a per-capsule basis to
 * achieve best results.
 * 
 * <h3>Academic References</h3>
 * <ol>
 * <li>Ganesha Upadhyaya and Hridesh Rajan, Effectively Mapping Linguistic Abstractions for Message-passing 
 * Concurrency to Threads on the Java Virtual Machine, OOPSLA'15
 * </ol>
 */
public enum ExecutionProfile {
    MOCKUP,
    THREAD,
    TASK,
    MONITOR,
    SERIAL,
}