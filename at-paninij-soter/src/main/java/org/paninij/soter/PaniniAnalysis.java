package org.paninij.soter;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.types.MethodReference;

import edu.illinois.soter.analysis.OwnershipTransferAnalysis;
import edu.illinois.soter.messagedata.MessageInvocation;

public class PaniniAnalysis extends OwnershipTransferAnalysis
{
    private static final Logger logger = Logger.getLogger(PaniniAnalysis.class.getName());
    
    private String capsule;
    
    public PaniniAnalysis(String capsule, String classpath)
    {
        super("Lorg/paninij/runtime/Panini$Capsule", classpath);
        this.capsule = capsule;
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
    
    public String getResultString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("PaniniAnalysis: capsule = " + capsule);
        buf.append("\n");
        for (Entry<CGNode, Map<CallSiteReference, MessageInvocation>> mapEntry : messageInvocations.entrySet()) {
            for (MessageInvocation messageInvocation : mapEntry.getValue().values()) {
                buf.append(messageInvocation.toString());
            }
        }
        return buf.toString();
    }
}
