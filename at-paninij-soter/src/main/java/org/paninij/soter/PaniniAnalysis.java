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
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeReference;

import edu.illinois.soter.analysis.OwnershipTransferAnalysis;
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
        // TODO Auto-generated method stub
        
    }

    @Override
    protected boolean isIgnoredField(IField field)
    {
        // TODO Auto-generated method stub
        return false;
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
            entrypoints.add(new DefaultEntrypoint(runDecl, classHierarchy));
        }
        else
        {
            // If passive, then every procedure is an entrypoint.
            getRelevantTemplateMethods()
                .stream()
                .filter(m -> isProcedure(m))
                .forEach(p -> entrypoints.add(new DefaultEntrypoint(p, classHierarchy)));
        }
    }

    @Override
    protected boolean isNodeConsideredForMessageInvocations(CGNode cgNode)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected int getMessageArgumentsIndex(MethodReference methodReference)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void addMessageInvocationArguments(MessageInvocation messageInvocation,
            int messagePassingIndex, int passedPointerValueNumber)
    {
        // TODO Auto-generated method stub
        
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
