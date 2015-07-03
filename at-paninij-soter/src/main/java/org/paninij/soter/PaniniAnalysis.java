package org.paninij.soter;

import java.util.Set;

import com.ibm.wala.classLoader.IField;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.types.MethodReference;

import edu.illinois.soter.analysis.OwnershipTransferAnalysis;
import edu.illinois.soter.messagedata.MessageInvocation;

public class PaniniAnalysis extends OwnershipTransferAnalysis
{

    public PaniniAnalysis(String capsuleName, String classPath)
    {
        super(capsuleName, classPath);
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
        // TODO Auto-generated method stub
        
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
    
}
