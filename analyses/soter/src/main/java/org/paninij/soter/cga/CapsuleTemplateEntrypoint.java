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
package org.paninij.soter.cga;

import static org.paninij.soter.util.PaniniModel.getCapsuleMockupClassReference;
import static org.paninij.soter.util.PaniniModel.getProceduresList;
import static org.paninij.soter.util.PaniniModel.getRunDecl;
import static org.paninij.soter.util.PaniniModel.isCapsuleCore;
import static org.paninij.soter.util.PaniniModel.isProcedure;
import static org.paninij.soter.util.SoterUtil.isKnownToBeEffectivelyImmutable;

import java.util.Set;
import java.util.function.Consumer;

import org.paninij.soter.util.PaniniModel;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.AbstractRootMethod;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.shrikeBT.IInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.collections.HashSetFactory;


/**
 * TODO: Consider re-implementing this with some sort of visitor over the capsule core.
 */
public class CapsuleCoreEntrypoint extends DefaultEntrypoint
{
    protected IClass core;
    protected int constZeroValueNumber;

    /**
     * @param method Either a `run()` declaration or a procedure on a well-formed capsule core
     *               (i.e. a class annotated with `@Capsule` which passed all `@PaniniJ` checks).
     */
    public CapsuleCoreEntrypoint(IMethod method)
    {
        super(method, method.getClassHierarchy());
        core = method.getDeclaringClass();
        if (core == null) {
            String msg = "Could not get declaring class of given method: " + method.getSignature();
            throw new NullPointerException(msg);
        }
        constZeroValueNumber = 0;
        if(isCapsuleCore(core) == false) {
            String msg = "Can't make entrypoints on classes unless they are cores.";
            throw new IllegalArgumentException(msg);
        }
    }


    /**
     * @param root  The fake root method to which the instantiated argument and any other required
     *              instantiations are being added.
     * @param i     The index of the argument being created, where 0 is the receiver object.
     * @return      The value number of the created argument instance; -1 if there was some error.
     */
    @Override
    protected int makeArgument(AbstractRootMethod root, int i)
    {
        return (i == 0) ? makeReceiver(root) : makeProcedureArgument(root, i);
    }
    
    
    /**
     * @see makeArgument
     */
    protected int makeReceiver(AbstractRootMethod root)
    {
        constZeroValueNumber = root.addLocal();
        
        // Instantiate a capsule core instance to serve as this entrypoint's receiver object.
        // Note that every capsule core must (only) have the default constructor.
        TypeReference receiverType = method.getParameterType(0);
        SSANewInstruction receiverAllocation = root.addAllocation(receiverType);
        if (receiverAllocation == null)
            return -1;
        int receiverValueNumber = receiverAllocation.getDef();
        
        // Make a capsule mockup instance for each of the receiver's fields (i.e. all of its
        // `@Local`, `@Import`, and state fields).
        for (IField f : core.getAllFields()) {
            addCoreFieldInstance(root, f, receiverValueNumber);
        }
        
        // Initialize the newly created receiver object.
        // TODO: Debug and re-enable this.
        makeReceiverInitInvocation(root, receiverValueNumber);
        
        return receiverValueNumber;
    }
    
    
    /**
     * @see makeArgument
     */
    protected int makeProcedureArgument(AbstractRootMethod root, int i)
    {
        // This should not be used to make a capsule core receiver object.
        assert i > 0;

        // Note that if this is called `method` cannot be a run decl and must be a procedure, since
        // run decls have exactly one argument: the capsule core reciever object.
        assert isProcedure(method);

        // TODO: Everything! But for now just use the default behavior.
        return super.makeArgument(root, i);
    }
    
    
    /**
     * Makes an invocation instruction on core object with value number `i`, and adds this
     * instruction to the given fake root method.
     * 
     * @see makeArgument
     */
    protected void makeReceiverInitInvocation(AbstractRootMethod root, int i)
    {
        IMethod initMethod = PaniniModel.getInitDecl(core);
        if (initMethod != null)
        {
            CallSiteReference initCall = CallSiteReference.make(root.getStatements().length,
                                                                initMethod.getReference(),
                                                                IInvokeInstruction.Dispatch.VIRTUAL);
            root.addInvocation(new int[] {i}, initCall);
        }
    }
    
    /**
     * Adds an instance to the core field's. The behavior of this method will depend upon the
     * field's type and annotations. For example, if a the field is primitive or effectively
     * immutable, nothing is instantiated; if it is a capsule interface, it will delegate to
     * `addCapsuleMockup()`.
     * 
     * @param root  The fake root method to which the instantiation instructions are being added.
     * @param field A field on `core`.
     * @param receiverValueNumber Value number of the receiver instance whose field to which some
     *                            kind of object instance is being added.
     */
    protected void addCoreFieldInstance(AbstractRootMethod root, IField field, int receiverValueNumber)
    {
        TypeReference fieldTypeRef = field.getFieldTypeReference();
        
        if (fieldTypeRef.isArrayType()) {
            addCoreFieldArrayInstance(root, field, receiverValueNumber);
            return;
        }

        if (fieldTypeRef.isPrimitiveType() || isKnownToBeEffectivelyImmutable(fieldTypeRef)) {
            // No need to add instances for these types.
            return;
        }

        if (PaniniModel.isCapsuleInterface(getCha().lookupClass(fieldTypeRef))) {
            addCapsuleMockupInstance(root, field, receiverValueNumber);
            return;
        }

        // Otherwise, consider the field to be non-capsule state variable.
        addStateInstance(root, field, receiverValueNumber);
    }
    
    
    /**
     * If appropriate, instantiate and add an array instance in the given fake root.
     */
    protected void addCoreFieldArrayInstance(AbstractRootMethod root, IField field,
                                                 int receiverValueNumber)
    {
        // Currently, when an array is instantiated it is instantiated with size 1, and when
        // elements need to be instantiated only the 0th is instantiated.
        // TODO: Is this bad?
        final int DEFAULT_ARRAY_LENGTH = 1;

        TypeReference arrayTypeRef = field.getFieldTypeReference();
        int dimensionality = arrayTypeRef.getDimensionality();
        if (dimensionality > 1)
        {
            String msg = "TODO: Cannot yet add instances for array whose dimensionality is ";
            throw new UnsupportedOperationException(msg + dimensionality);
        }
        
        SSAInstruction newArrayInstr = root.add1DArrayAllocation(arrayTypeRef, DEFAULT_ARRAY_LENGTH);

        TypeReference elemTypeRef = arrayTypeRef.getArrayElementType();
        if (elemTypeRef.isPrimitiveType() || isKnownToBeEffectivelyImmutable(elemTypeRef))
        {
            // No need to add instances for the elements of these types of arrays.
            return;
        }

        // Add a single new mockup instance if the array's elements are capsule interfaces.
        if (PaniniModel.isCapsuleInterface(getCha().lookupClass(elemTypeRef)))
        {
            int mockupValueNumber = newCapsuleMockupInstance(root, elemTypeRef);
            root.addSetArrayField(elemTypeRef, newArrayInstr.getDef(), constZeroValueNumber,
                                  mockupValueNumber);
            return;
        }
        
        // Otherwise, assume that the elements of the array should be initialized in source code.
        // TODO: Is this right.
    }
    
    
    /**
     * Instantiates a capsule mockup for this capsule core field.
     * 
     * @param root  The fake root method to which the instantiation instructions are being added.
     * @param field A field whose declaring class is a well-formed capsule interface (i.e. a class
     *              generated by `@PaniniJ` and is annotated with `@CapsuleInterface`).
     * @param receiverValueNumber Value number of the receiver instance whose field is being
     *                            instantiated with a capsule mockup class.
     */
    protected void addCapsuleMockupInstance(AbstractRootMethod root, IField field, int receiverValueNumber)
    {
        int mockupValueNumber = newCapsuleMockupInstance(root, field.getFieldTypeReference());
        root.addSetInstance(field.getReference(), receiverValueNumber, mockupValueNumber);
    }
    
    
    protected int newCapsuleMockupInstance(AbstractRootMethod root, TypeReference interfaceTypeRef)
    {
        TypeReference mockupType = getCapsuleMockupClassReference(interfaceTypeRef);
        if (mockupType == null)
        {
            String msg = "Could not load the mockup class associated with " + interfaceTypeRef;
            throw new IllegalArgumentException(msg);
        }

        // There's no need to call the mockup's constructor.
        SSANewInstruction mockupAlloc = root.addAllocationWithoutCtor(mockupType);
        if (mockupAlloc == null)
        {
            // This may happen if the mockup class could not be found.
            String msg = "Failed to create an allocation for a mockup: " + mockupType;
            throw new RuntimeException(msg);
        }
        return mockupAlloc.getDef();
    }
    

    /**
     * Transitively instantiates the state associated with this capsule core field.
     * 
     * @param root            The fake root method to which the new instruction is being added.
     * @param field           A field whose declaring class is considered a "state" by `@PaniniJ`.
     * @param instValueNumber Value number of the object instance whose field is being instantiated
     */
    protected void addStateInstance(AbstractRootMethod root, IField field, int instValueNumber)
    {
        // TODO: This is currently disabled based on the idea that artificial addition of
        // (non-imported) state is unnecessary. All state instances should either be wired in,
        // instantiated in the `init()` method, or instantiated at the beginning of `run()`.

        // TODO: Consider somehow using `DefaultEntrypoint.makeArgument()` here.
        // TODO: This does not yet transitively (a.k.a. recursively) instantiate state.
        /*
        TypeReference fieldType = field.getFieldTypeReference();
        if (fieldType == null)
        {
            String msg = "Failed to look up a field's `TypeReference`: " + field;
            throw new RuntimeException(msg);
        }

        // No need to add instances for primitives or objects which known to be truly safe.
        if (fieldType.isPrimitiveType() || isKnownToBeEffectivelyImmutable(fieldType)) {
            return;
        }
        
        SSANewInstruction stateAlloc = root.addAllocation(fieldType);
        if (stateAlloc == null)
        {
            String msg = "While adding a state for the capsule core `{0}`, a 'new' instruction "
                       + "could not be added to the fake root for the field `{1}` whose "
                       + "`TypeReference` is `{2}`";
            throw new RuntimeException(MessageFormat.format(msg, core, field, fieldType));
        }

        int stateValueNumber = stateAlloc.getDef();
        root.addSetInstance(field.getReference(), instValueNumber, stateValueNumber);
        */
    }
    
    
    /**
     * Returns a set of all of the entrypoints on the given capsule core.
     * 
     * @param core A well-formed capsule core class, annotated with `@Capsule`.
     */
    public static Set<Entrypoint> makeAll(IClass core)
    {
        assert isCapsuleCore(core);

        Set<Entrypoint> entrypoints = HashSetFactory.make();
        final Consumer<IMethod> addEntrypoint = (m -> entrypoints.add(new CapsuleCoreEntrypoint(m)));

        // The way in which `entrypoints` is populated depends on whether the capsule core 
        // defines an active or passive capsule. If active, then the only entrypoint is `run()`.
        // If passive, then every procedure is an entrypoint.
        IMethod runDecl = getRunDecl(core);
        if (runDecl != null) {
            addEntrypoint.accept(runDecl);
        } else {
            getProceduresList(core).forEach(addEntrypoint);
        }
        return entrypoints;
    }
}
