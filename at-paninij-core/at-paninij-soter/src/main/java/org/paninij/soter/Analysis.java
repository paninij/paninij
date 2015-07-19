package org.paninij.soter;

import java.util.Map;
import java.util.Set;

import org.paninij.soter.cfa.CallGraphAnalysis;
import org.paninij.soter.cfa.TransferSitesMapBuilder;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.model.TransferSite;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class Analysis
{
    protected CapsuleTemplate capsule;
    protected CallGraphAnalysis cfa;
    protected IClassHierarchy cha;

    protected Map<CGNode, Set<TransferSite>> transferSitesMap;

    public Analysis(CapsuleTemplate capsule, CallGraphAnalysis cfa, IClassHierarchy cha)
    {
        this.capsule = capsule;
        this.cfa = cfa;
        this.cha = cha;
    }
    
    public void perform()
    {
        buildTransferSitesMap();
    }

    /**
     * Builds `transferSites` and the associated instance variable, `transferingNodes`.
     */
    protected void buildTransferSitesMap()
    {
        TransferSitesMapBuilder builder = new TransferSitesMapBuilder(cha);
        
        for (CGNode node : cfa.getCallGraph())
        {
            // Only add transfer sites from nodes whose methods are declared directly on the capsule
            // template. Ignore any others. This is done because transfer points can only be defined
            // within the capsule template itself.
            if (capsule.getTemplate().equals(node.getMethod().getDeclaringClass())) {
                builder.addTransferSitesFrom(node);
            }
        }
        
        transferSitesMap = builder.getTransferSitesMap();
    }

    /**
     * @return The zero-one CFA call graph starting from the capsule being analyzed.
     */
    public CallGraph getCallGraph()
    {
        return cfa.getCallGraph();
    }
    
    /**
     * @return The capsule being analyzed.
     */
    public CapsuleTemplate getCapsule()
    {
        return capsule;
    }

    /**
     * @return The set of nodes which were found in the analysis to have a transfer point.
     */
    public Set<CGNode> getTransferringNodes()
    {
        return transferSitesMap.keySet();
    }
    
    /**
     * @return The set of transfer sites found within the given call graph node or `null` if there
     *         are no sites associated with this node.
     */
    public Set<TransferSite> getTransferSites(CGNode node)
    {
        return transferSitesMap.get(node);
    }
 }
