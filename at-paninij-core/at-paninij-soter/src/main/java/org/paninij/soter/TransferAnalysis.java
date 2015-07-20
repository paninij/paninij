package org.paninij.soter;

import org.paninij.soter.cfa.CallGraphAnalysis;
import org.paninij.soter.model.CapsuleTemplate;

import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class TransferAnalysis
{
    protected CapsuleTemplate capsule;
    protected CallGraphAnalysis cfa;
    protected IClassHierarchy cha;

    public TransferAnalysis(CapsuleTemplate capsule, CallGraphAnalysis cfa, IClassHierarchy cha)
    {
        this.capsule = capsule;
        this.cfa = cfa;
        this.cha = cha;
    }

    public void perform()
    {
        cfa.perform();
        // TODO: Use site live analysis factory
    }

    public CallGraph getCallGraph()
    {
        return cfa.getCallGraph();
    }
 }
