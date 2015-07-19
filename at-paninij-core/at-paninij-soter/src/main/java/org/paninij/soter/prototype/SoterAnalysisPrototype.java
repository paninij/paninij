package org.paninij.soter.prototype;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.paninij.soter.cfa.CapsuleTemplateEntrypoint;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeReference;

import edu.illinois.soter.analysis.OwnershipTransferAnalysis;
import edu.illinois.soter.messagedata.MessageInvocation;
import static org.paninij.soter.util.PaniniModel.*;


public class SoterAnalysisPrototype extends OwnershipTransferAnalysis
{
    private static final Logger logger = Logger.getLogger(SoterAnalysisPrototype.class.getName());
    
    protected String templateName;
    protected TypeReference templateTypeReference;
    protected IClass templateClass;
    
    public SoterAnalysisPrototype(String templateName, String classpath)
    {
        // Note: Ideally, the `templateClass` would be initialized in the constructor, but this
        // is not possible, since at construction time, the WALA classloader is not yet available.
        // The order-dependent lazy evaluation in `OwnershipTransferAnalysis` seems like a design
        // bug.
        super("Lorg/paninij/runtime/Panini$Capsule", classpath);
        this.templateName = templateName;
    }
    
    @Override
    protected void setupIgnoredFields()
    {
        // Nothing to do, since no template fields (i.e. states) should be ignored.
    }

    @Override
    protected boolean isIgnoredField(IField field)
    {
        // Never ignore, since no template fields (i.e. states) should be ignored.
        return false;
    }

    @Override
    protected void populateEntrypoints(Set<Entrypoint> entrypoints)
    {
        initTemplateClass();  // Note: The first point at which `templateClass` can be initialized.

        IMethod runDecl = getRunDecl(templateClass);
        Consumer<IMethod> addEntrypoint =
            (m -> entrypoints.add(new CapsuleTemplateEntrypoint(m)));

        // The way in which `entrypoints` is populated depends on whether the capsule template 
        // defines an active or passive capsule. If active, then the only entrypoint is `run()`.
        // If passive, then every procedure is an entrypoint.
        if (runDecl != null) {
            addEntrypoint.accept(runDecl); }
        else {
            getProceduresList(templateClass).forEach(addEntrypoint);
        }
    }

    /**
     * This returns true if the given call graph node might invoke procedures on another capsule
     * (i.e. `cgNode` represents a method on `templateClass` which might invoke a procedure on some
     * remote capsule).
     */
    @Override
    protected boolean isNodeConsideredForMessageInvocations(CGNode cgNode)
    {
        // Consider only those call graph nodes which are methods of the template being analyzed.
        IClass declaringClass = cgNode.getMethod().getDeclaringClass();
        return declaringClass.equals(templateClass);
    }

    /**
     * This method returns 1, the parameter index of the first message argument *if*
     * `methodReference` is an invocation of a remote procedure when called from the template being
     * analyzed. Otherwise, when `methodReference` wouldn't be called as a remote procedure, -1 is
     * returned.
     * 
     * TODO: This abstract method and how it fits into the original SOTER analysis does not work
     * with @PaniniJ, where every parameter of a procedure is part of the message, not just one
     * parameter (as in ActorFoundry). This is currently a design bug.
     * 
     * TODO: The above description does not cover the behavior of procedures with zero args. These
     * cases return -1 because they are inherently "safe".
     * 
     * @throws UnsupportedOperationException when there is more than one parameter which is part of
     * the message.
     */
    @Override
    protected int getMessageArgumentsIndex(MethodReference methodReference)
    {
        // A method reference is assumed to be a remote procedure invocation whenever the reference
        // to the receiver is a capsule interface.
        // TODO: Is this too brittle?

        TypeReference receiverReference = methodReference.getDeclaringClass();
        IClass receiverClass = classHierarchy.lookupClass(receiverReference);
        if (receiverClass == null) {
            String msg = "Failed to lookup class of given `MethodReference`: " + methodReference;
            throw new IllegalArgumentException(msg);
        }

        if (isCapsuleInterface(receiverClass))
        {
            // Note: Unlike in some WALA methods, the below `getNumberOfParameters()` does not count
            // the `this` receiver object in the count of parameters.

            switch (methodReference.getNumberOfParameters())
            {
            case 0:
                return -1;  // If a procedure has no parameters, it is always considered safe.
            case 1:
                return 1;  // The index that we are returning assumes a 0th receiver object.
            default:
                String msg = "We only support procedure invocations with zero or one parameters.";
                throw new UnsupportedOperationException(msg);
            }
        }
        else
        {
            return -1;
        }
    }


    @Override
    protected void addMessageInvocationArguments(MessageInvocation messageInvocation,
                                                 int messagePassingIndex,
                                                 int passedPointerValueNumber)
    {
        CGNode cgNode = messageInvocation.getCGNode();
        SSAInstruction[] instructions = cgNode.getIR().getInstructions();

        // WARNING: This assumes that there is exactly one argument to any @PaniniJ procedure being
        // analyzed. Procedures of zero arguments are inherently safe and support for procedures
        // with two or more arguments has not yet been added.

        for (int idx = messagePassingIndex; idx > 0; idx--)
        {
            SSAInstruction instruction = instructions[idx];
            if (instruction != null && instruction.hasDef())
            {
                if (instruction.getDef() == passedPointerValueNumber)
                {
                    PointerKey ptr = heapModel.getPointerKeyForLocal(cgNode, passedPointerValueNumber);
                    Set<Object> reaching = computeTransitiveClosureOfPointedItems(ptr);
                    // TODO: Why doesn't the transitive closure work at all. It doesn't even include
                    // `ptr` itself!
                    // WARNING: Here's an attempted workaround for debugging, add `ptr` manually:
                    reaching.add(basicHeapGraph.getNode(basicHeapGraph.getNumber(ptr)));
                    messageInvocation.addFirstArgument(idx, reaching);
                    return;
                }
            }
        }
        
        String msg = "Could not find a def associated with the given `passedPointerValueNumber.";
        throw new IllegalArgumentException(msg);
    }

     
    protected TypeReference getTemplateTypeReference()
    {
        if (templateTypeReference == null) {
            templateTypeReference = getReferenceForTypeName(templateName);
        }
        return templateTypeReference;
    }
    
    /**
     * Expects that the analysis has already been initialized (or at least that `classHierarchy` is
     * non-null).
     */
    protected void initTemplateClass()
    {
        if (templateClass == null)
        {
            templateClass = classHierarchy.lookupClass(getTemplateTypeReference());
            if (templateClass == null) {
                String msg = "Lookup of a template class failed: " + templateName;
                throw new IllegalArgumentException(msg);
            }
        }
    }
    
   
    public String getResultString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("PaniniAnalysis: capsule = " + templateName);
        buf.append("\n");
        for (Entry<CGNode, Map<CallSiteReference, MessageInvocation>> mapEntry : messageInvocations.entrySet()) {
            for (MessageInvocation messageInvocation : mapEntry.getValue().values()) {
                buf.append(messageInvocation.toString());
            }
        }
        return buf.toString();
    }
    
    
    public CallGraph getCallGraph() {
        return callGraph;
    }
}
