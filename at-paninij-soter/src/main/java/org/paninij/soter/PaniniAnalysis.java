package org.paninij.soter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeReference;

import edu.illinois.soter.analysis.OwnershipTransferAnalysis;
import edu.illinois.soter.analysis.actorfoundry.SingleInstanceEntrypoint;
import edu.illinois.soter.messagedata.MessageInvocation;
import static org.paninij.soter.PaniniModel.*;

public class PaniniAnalysis extends OwnershipTransferAnalysis
{
    private static final Logger logger = Logger.getLogger(PaniniAnalysis.class.getName());
    
    protected String templateName;
    protected TypeReference templateTypeReference;
    protected IClass templateClass;
    
    public PaniniAnalysis(String templateName, String classpath)
    {
        super("Lorg/paninij/runtime/Panini$Capsule", classpath);
        this.templateName = templateName;
    }
    
    @Override
    protected void setupIgnoredFields()
    {
        // TODO: Everything
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    protected boolean isIgnoredField(IField field)
    {
        // TODO: Everything
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    protected void populateEntrypoints(Set<Entrypoint> entrypoints)
    {
        // How `entrypoints` is populated depends upon whether the capsule is active or passive.
        if (templateIsActive(getTemplateClass()))
        {
            // If active, then the only entrypoint is `run()`.
            IMethod runDecl = getRelevantTemplateMethods()
                                .stream()
                                .filter(m -> isRunDecl(m))
                                .findFirst()
                                .get();
            entrypoints.add(new SingleInstanceEntrypoint(templateClass, runDecl, classHierarchy));
        }
        else
        {
            // If passive, then every procedure is an entrypoint.
            getRelevantTemplateMethods()
                .stream()
                .filter(m -> isProcedure(m))
                .forEach(p -> entrypoints.add(new SingleInstanceEntrypoint(templateClass, p,
                                                                           classHierarchy)));
        }
    }

    /**
     * This returns true if the given call graph node may invoke procedures on another capsule.
     */
    @Override
    protected boolean isNodeConsideredForMessageInvocations(CGNode cgNode)
    {
        // Consider only those call graph nodes which are methods of the template being analyzed.
        IClass declaringClass = cgNode.getMethod().getDeclaringClass();
        return declaringClass.equals(templateClass);
    }

    /**
     * This method returns the number of arguments in the method given method if `methodReference`
     * is an invocation of a remote procedure when called from the template being analyzed.
     * Otherwise, whenever `methodReference` wouldn't be called as a remote procedure, -1 is
     * returned.
     */
    @Override
    protected int getMessageArgumentsIndex(MethodReference methodReference)
    {
        IMethod resolved = classHierarchy.resolveMethod(methodReference);

        if (isRemoteProcedure(templateClass, resolved)) {
            return resolved.getNumberOfParameters();
        } else {
            return -1;
        }
    }

    @Override
    protected void addMessageInvocationArguments(MessageInvocation messageInvocation,
                                                 int messagePassingIndex,
                                                 int passedPointerValueNumber)
    {
        // TODO: Everything
        throw new UnsupportedOperationException("TODO");
    }
     
    protected TypeReference getTemplateTypeReference()
    {
        if (templateTypeReference == null) {
            templateTypeReference = getReferenceForTypeName(templateName);
        }
        return templateTypeReference;
    }
    
    protected IClass getTemplateClass()
    {
        if (templateClass == null)
        {
            templateClass = classHierarchy.lookupClass(getTemplateTypeReference());
            if (templateClass == null) {
                String msg = "Lookup of a template class failed: " + templateName;
                throw new IllegalArgumentException(msg);
            }
        }
        return templateClass;
    }
    
   
    protected List<IMethod> getRelevantTemplateMethods()
    {
        return getTemplateClass()
                 .getAllMethods()
                 .stream()
                 .filter(m -> isRelevantTemplateMethod(m))
                 .collect(toList());
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
}
