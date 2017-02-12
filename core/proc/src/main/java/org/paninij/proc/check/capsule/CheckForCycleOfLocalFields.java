package org.paninij.proc.check.capsule;

import org.paninij.proc.check.Check;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.type.TypeKind.ARRAY;
import static javax.lang.model.type.TypeKind.DECLARED;
import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;
import static org.paninij.proc.util.JavaModel.isAnnotatedBy;
import static org.paninij.proc.util.PaniniModel.CAPSULE_CORE_SUFFIX;

/**
 * <p>Checks that a given capsule core is not a part a part some cycle of capsule types linked
 * by {@code @Local} fields.
 *
 * <p>This check is needed to prevent the definition of certain uninstantiable capsule systems. To
 * see the problem, consider one such uninstantiable system:
 *
 * <pre><code>
 * import org.paninij.lang.*;
 * &#64;Capsule
 * class CycleCore {
 *     &#64;Local Cycle cycle;
 * }
 * </code></pre>
 *
 * <p>Just before a capsule system containing this capsule begins, the runtime will instantiate one
 * {@code Cycle} capsule instance. Because this {@code @Cycle} instance has an {@code @Local Cycle}
 * field, a second instance of {@code Cycle} is needed as well. This second one is responsible for a
 * third, and so on without bound. Thus, capsule allocation proceeds until some resource limit is
 * reached and the system crashes.
 *
 * <p>We can imagine the problem as the existence of a cycle in a directed graph whose vertices are
 * capsule core types and whose edges are {@code @Local} fields pointing between them. In the
 * case above, we can imagine that this capsule core is contributing to the graph a single
 * vertex with a self-edge.
 *
 * <p>In our example, our capsule needs to instantiate another instance of the same capsule type,
 * which leads to infinite regress. However, we can see the problem more generally as a problem of
 * cycles of cores. Our example capsule core is a part of a cycle of length one, but the
 * same hazard of infinite regress occurs for cycles of any length.
 *
 * <p>Fortunately, because of the static nature of capsule system topologies, it is easy to check
 * for this possibility at compile time and to thus eliminate the possibility of it happening at
 * runtime. This {@link CapsuleCheck} is responsible for doing just that.
 *
 * <p>The check is implemented by recursively exploring capsule core definitions across their
 * {@code @Local} fields. We don't explicitly construct the graph, but rather explore the
 * {@link TypeElement} data structures which encode the user's capsule core types. In
 * particular, we use a customized depth-first traversal to explore these links between capsule
 * core types looking for cycles. If while exploring in this way we re-find the capsule type at
 * which we started, the {@link Check.Result} is an error result. If the query capsule core was
 * not found to be in a cycle, then the result is {@link Result#OK}.
 *
 * <p>Unlike most checks, this one maintains some state throughout its lifetime: we memoize the
 * results from our explorations checked capsule cores. This speeds up checks by preventing the
 * re-exploration of capsules which have previously been checked. These partial results are used
 * both within and across invocations of the check.
 *
 * <p>While exhaustively searching for cycles which involve some query capsule core, we may
 * discover a cycle between some <em>other</em> capsule cores. Related to this observation, we
 * have designed this check with two useful properties:
 *
 * <ul>
 *     <li>Whenever a cycle discovered, all capsule cores along that cycle are marked as error,
 *     even if that cycle does not include the original query capsule core.</li>
 *     <li>Even if a cycle is discovered somewhere in the graph during an exploration, the
 *     query capsule core will not necessarily be marked as an error.</li>
 * </ul>
 *
 * <p>The first property is meant to speed up execution by saving such results. The second property
 * is meant to keep blame for a cycle small to thus keep error messages to the user to a minimum;
 * the principle is that a capsule core should only be marked as an error if that capsule
 * core is directly involved in some cycle in the graph. In the current implementation, we do
 * not report the cycles themselves to the user. Instead, we just give an error for each capsule
 * core participating in the cycle in no particular order.
 *
 * TODO: Use or adapt this blurb:
 * The main observation to understanding this recursive algorithm works is the
 * following: if the capsule core associated with some field `f` has been fully
 * searched and found to be `OK` (i.e. no cycles found), then there must not be any
 * cycle including these two capsules. This means that we need not recurse down those
 * edges to capsules which have been found to be `OK`: any cycles in which `core`
 * is a part cannot involve these `OK` neighbors. So, to determine whether `core` is
 * `OK`, we only need to check for this capsule's involvement in a cycle via those
 * fields whose cores are as-yet unchecked or are part of a cycle.
 *
 * <em>TODO:</em> State which other capsule checks this one depends upon.
 *
 * @author dwtj
 */
public class CheckForCycleOfLocalFields implements CapsuleCheck {

    private final ProcessingEnvironment procEnv;

    /**
     * Maps a fully qualified name of a previously-checked capsule to the results of those
     * checks. These results are built up incrementally as checks are performed. In the current
     * design, all prior results are kept for the lifetime of this check object instance.
     */
    private final Map<String, Result> results = new HashMap<>();

    public CheckForCycleOfLocalFields(ProcessingEnvironment procEnv) {
        this.procEnv = procEnv;
    }

    @Override
    public Result checkCapsule(TypeElement core) {
        assert isAnnotatedBy(procEnv, core, "org.paninij.lang.Capsule");
        assert core.getKind() == CLASS;
        Result memoized = results.get(key(core));
        if (memoized != null) {
            return memoized;
        }
        searchForCyclesAt(core, new Stack<>());
        return results.get(key(core));
    }

    /**
     * A recursive procedure to search for cycles. The procedure terminates either when it has found
     * a path from {@code core} to itself or when it has performed an exhaustive search to show
     * that no such path exists. Furthermore, we guarantee that after termination, all cores
     * reachable from {@code core} will be correctly marked with either {@link Result#error}
     * or {@link Result#OK}.
     *
     * Whenever some cycle is found during this process (whether or not it be a cycle including
     * {@code core}), the procedure marks that cycle's elements in {@link #results}
     * appropriately. Thus, at termination, if a cycle was found including {@code core}, then
     * core (along with all of the other items in the found cycle) has been marked with an error
     * in {@link #results}. If no such cycle was found for {@code core} during the search, then
     * {@code core} will be marked with {@link Result#OK} in {@link #results}. Other cores
     * may also be marked with {@link Result#OK} as a side-effect.
     *
     * @param cur
     *          The capsule core through which we are currently trying to find cycles.
     * @param searchPath
     *          The path that has been explored so far, from the original query capsule core
     *          up to (but not including) `cur`.
     */
    private void searchForCyclesAt(TypeElement cur, Stack<String> searchPath) {
        if (markCycleIfAny(cur, searchPath)) {
            // [Base Case] If the tail of the `searchPath` forms a cycle, then mark all the
            // elements in this cycle as errors. From here, execution does not recurse with an
            // extended search path. Thus, there is nothing else to do here: all of the work
            // was all done as part of `#markCycleIfAny()`.
            return;
        }

        // [Recursive Case] For each neighbor of `cur`, search for cycles which include both
        // `cur` and that neighbor. We can skip any neighbors marked with `Result#OK`; if my
        // analysis is correct, it is not *correct* to skip neighbors marked with
        // `Result#error`.
        for (TypeElement successor : getSuccessors(cur)) {
            if (results.get(key(successor)) == OK) {
                continue;
            }
            searchPath.push(key(cur));
            searchForCyclesAt(successor, searchPath);
            searchPath.pop();
        }
        // If after we have searched for cycles along all neighbors and no paths including `cur`
        // were discovered, then we mark `cur` as `OK`.
        results.putIfAbsent(key(cur), OK);
    }

    /**
     * A helper method for {@link #searchForCyclesAt(TypeElement, Stack)} which is responsible
     * for detecting and marking cycles along the given {@code searchPath}.
     *
     * @param cur
     *      The capsule core which would appear at the beginning and end of the cycle, if one
     *      were to be detected.
     * @param searchPath
     *      The sequence of capsule core keys from the original query up to (but not including)
     *      {@code cur}.
     * @return
     *      {@code true} iff a cycle was found in the suffix of {@code searchPath} from {@code
     *      cur} to itself.
     */
    private boolean markCycleIfAny(TypeElement cur, Stack<String> searchPath) {
        int searchDist = searchPath.search(key(cur));
        boolean hasCycle = searchDist > 0;
        if (hasCycle) {
            int pathStartIdx = searchPath.size() - searchDist;
            for (int idx = pathStartIdx; idx < searchPath.size(); idx++) {
                String key = searchPath.get(idx);
                Result prevResult = results.get(key);
                if (prevResult != null) {
                    assert prevResult != OK;
                    continue;  // This capsule core has already been marked with an error.
                }
                String errMsg = "Capsule core is part of an illegal `@Local` cycle: " + key;
                results.put(key, error(errMsg, CheckForCycleOfLocalFields.class, cur));
            }
        }
        return hasCycle;
    }

    /**
     * Returns a set of unique successors to the given capsule core. If {@code x} and {@code y}
     * are capsule cores and if {@code x} includes some {@code @Local} field whose type is the
     * capsule interface associated with {@code y}, then {@code x} succeeds {@code y}. The term
     * {@em successor} is used because we can imagine that an {@code @Local} edge implies an edge
     * going from {@code x} to each of its successors {@code y}.
     *
     * @param capsuleCore
     * @return
     */
    private Set<TypeElement> getSuccessors(TypeElement capsuleCore) {
        Set<TypeElement> successors = new HashSet<>();
        for (Element elem : capsuleCore.getEnclosedElements()) {
            if (elem.getKind() == FIELD && isAnnotatedBy(procEnv, elem, "org.paninij.lang.Local")) {
                addIfAbsent(successors, lookupCapsuleCore(toScalarType(elem.asType())));
            }
        }
        return successors;
    }

    private static TypeMirror toScalarType(TypeMirror t) {
        return (t.getKind() != ARRAY) ? t : toScalarType(((ArrayType) t).getComponentType());
    }

    private TypeElement lookupCapsuleCore(TypeMirror capsule) {
        assert capsule.getKind() == DECLARED;
        assert isAnnotatedBy(procEnv, capsule, "org.paninij.lang.CapsuleInterface");
        String coreName = capsule.toString() + CAPSULE_CORE_SUFFIX;
        TypeElement core = procEnv.getElementUtils().getTypeElement(coreName);
        if (core == null) {
            throw new IllegalStateException("Failed to lookup a capsule core: " + coreName);
        }
        return core;
    }

    private void addIfAbsent(Set<TypeElement> set, TypeElement newElem) {
        Types types = procEnv.getTypeUtils();
        for (TypeElement oldElem : set) {
            if (types.isSameType(oldElem.asType(), newElem.asType())) {
                // Return now, since a type element equivalent to `newElem` is already in the set.
                return;
            }
        }
        set.add(newElem);
    }

    /**
     * Converts the given capsule core to the key used to canonically represent it in this
     * class's data structures (e.g. {@link #results} and path stacks).
     */
    private String key(TypeElement core) {
        assert core.getKind() == CLASS;
        assert isAnnotatedBy(procEnv, core, "org.paninij.lang.Capsule");
        return core.getQualifiedName().toString();
    }
}
