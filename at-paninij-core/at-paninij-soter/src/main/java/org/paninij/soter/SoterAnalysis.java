package org.paninij.soter;

import static org.paninij.soter.util.SoterUtil.makePointsToClosure;
import static java.text.MessageFormat.format;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.paninij.runtime.util.IdentitySet;
import org.paninij.runtime.util.IntMap;
import org.paninij.soter.cga.CallGraphAnalysis;
import org.paninij.soter.live.CallGraphLiveAnalysis;
import org.paninij.soter.live.TransferLiveAnalysis;
import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.transfer.TransferAnalysis;
import org.paninij.soter.transfer.TransferSite;
import org.paninij.soter.transfer.TransferSiteFactory;
import org.paninij.soter.util.Analysis;

import com.ibm.wala.analysis.pointers.HeapGraph;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.ArrayContentsKey;
import com.ibm.wala.ipa.callgraph.propagation.HeapModel;
import com.ibm.wala.ipa.callgraph.propagation.InstanceFieldKey;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.LocalPointerKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.callgraph.propagation.StaticFieldKey;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.intset.IntIterator;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.util.intset.MutableIntSet;
import com.ibm.wala.util.intset.MutableSparseIntSetFactory;

public class SoterAnalysis extends Analysis
{
    // The analysis's dependencies:
    protected final CapsuleTemplate template;
    protected final CallGraphAnalysis cga;
    protected final TransferAnalysis ta;
    protected final TransferLiveAnalysis tla;
    protected final CallGraphLiveAnalysis cgla;
    protected final IClassHierarchy cha;


    protected final MutableSparseIntSetFactory intSetFactory;

    /**
     * This analysis adds its results to this map as they are generated. The keys are the
     * transferring `TransferSite` objects generated by the `TransferAnalysis` and values are
     * generated `TransferSiteResults` object.
     */
    protected final Map<TransferSite, TransferSiteResults> transferSiteResultsMap;


    protected final Map<IMethod, IdentitySet<TransferSite>> unsafeTransferSitesMap;


    protected final JsonCreator jsonCreator;


    public SoterAnalysis(CapsuleTemplate template, CallGraphAnalysis cga, TransferAnalysis ta,
                         TransferLiveAnalysis tla, CallGraphLiveAnalysis cgla, IClassHierarchy cha)
    {
        this.template = template;
        this.cga = cga;
        this.ta = ta;
        this.tla = tla;
        this.cgla = cgla;
        this.cha = cha;

        intSetFactory = new MutableSparseIntSetFactory();

        transferSiteResultsMap = new HashMap<TransferSite, TransferSiteResults>();
        unsafeTransferSitesMap = new HashMap<IMethod, IdentitySet<TransferSite>>();

        jsonCreator = new JsonCreator(this);
    }


    @Override
    protected void performSubAnalyses()
    {
        cga.perform();
        ta.perform();
        tla.perform();
        cgla.perform();
    }


    @Override
    protected void performAnalysis()
    {
        buildTransferSiteResultsMap();
        buildUnsafeTransfersMap();
    }


    protected void buildTransferSiteResultsMap()
    {
        for (CGNode transferringNode : ta.getTransferringNodes())
        {
            for (TransferSite transferSite : ta.getTransferringSites(transferringNode))
            {
                TransferSiteResults results = new TransferSiteResults();

                // Find all of the live variables for this transfer site.
                results.liveVariables = new HashSet<PointerKey>();
                results.liveVariables.addAll(tla.getPointerKeysAfter(transferSite));
                results.liveVariables.addAll(cgla.getPointerKeysAfter(transferringNode));

                // Find all of the (transitively) live objects.
                results.liveObjects = new IdentitySet<InstanceKey>();
                for (PointerKey pointerKey : results.liveVariables) {
                    results.liveObjects.addAll(makePointsToClosure(pointerKey, cga.getHeapGraph()));
                }

                // For each of the transfer site's transfers, find all of the (transitively)
                // escaped objects.
                HeapModel heapModel = cga.getHeapModel();
                HeapGraph<InstanceKey> heapGraph = cga.getHeapGraph();
                IntIterator paramIter = transferSite.getTransfers().intIterator();
                while (paramIter.hasNext())
                {
                    int paramID = paramIter.next();

                    PointerKey ptr = heapModel.getPointerKeyForLocal(transferringNode, paramID);
                    IdentitySet<InstanceKey> escaped = makePointsToClosure(ptr, heapGraph);
                    results.setEscapedObjects(paramID, escaped);

                    boolean isSafeTransfer = results.liveObjects.isDisjointFrom(escaped);
                    results.setTransferSafety(paramID, isSafeTransfer);
                }

                transferSiteResultsMap.put(transferSite, results);
            }
        }
    }


    protected void buildUnsafeTransfersMap()
    {
        for (Entry<TransferSite, TransferSiteResults> entry : transferSiteResultsMap.entrySet())
        {
            TransferSite transferSite = entry.getKey();
            TransferSiteResults results = entry.getValue();

            if (results.hasUnsafeTransfers())
            {
                IMethod method = transferSite.getNode().getMethod();
                IdentitySet<TransferSite> unsafeTransferSites = getOrMakeUnsafeTransferSites(method);
                TransferSite unsafeTransferSite = TransferSiteFactory.copyWith(transferSite, results.getUnsafeTransfers());
                unsafeTransferSites.add(unsafeTransferSite);
            }
        }
    }

    /**
     * Gets and returns the set of unsafe transfer sites associated with the given method. If there
     * isn't yet such a set in the map, then an empty set is created, added to the map, and
     * returned.
     */
    private IdentitySet<TransferSite> getOrMakeUnsafeTransferSites(IMethod method)
    {
        IdentitySet<TransferSite> unsafeTransferSites = unsafeTransferSitesMap.get(method);
        if (unsafeTransferSites == null)
        {
            unsafeTransferSites = new IdentitySet<TransferSite>();
            unsafeTransferSitesMap.put(method, unsafeTransferSites);
        }
        return unsafeTransferSites;
    }


    public CallGraph getCallGraph()
    {
        return cga.getCallGraph();
    }


    public HeapGraph<InstanceKey> getHeapGraph()
    {
        return cga.getHeapGraph();
    } 


    public CapsuleTemplate getCapsuleTemplate()
    {
        return template;
    }


    /**
     * @return The map of primary results generated by this analysis. The map's keys are methods on
     *         the capsule template which have been found to have unsafe transfer sites. The value
     *         associated with a method is the set of transfer sites within this method which have
     *         been found by the analysis to have unsafe transfers. The `transfers` on these
     *         `TransferSite` instances are the set of transfers which were found to be *unsafe*
     *         (rather than all of the transfers at that transfer site).
     * 
     * TODO: Consider removing this from the interface.
     */
    public Map<IMethod, IdentitySet<TransferSite>> getUnsafeTransferSitesMap()
    {
        return unsafeTransferSitesMap;
    }


    public JsonObject toJson()
    {
        return jsonCreator.toJson();
    }


    public String toJsonString()
    {
        return jsonCreator.toJsonString();
    }

    /**
     * A simple container class to hold all of the results which the analysis generates for a single
     * transfer site.
     */
    private final class TransferSiteResults
    {
        Set<PointerKey> liveVariables;
        IdentitySet<InstanceKey> liveObjects;
        MutableIntSet unsafeTransfers;
        MutableIntSet safeTransfers;
        IntMap<IdentitySet<InstanceKey>> escapedObjectsMap;

        public TransferSiteResults()
        {
            unsafeTransfers = intSetFactory.make();
            safeTransfers = intSetFactory.make();
            escapedObjectsMap = new IntMap<IdentitySet<InstanceKey>>();
        }

        public void setEscapedObjects(int transferID, IdentitySet<InstanceKey> escapedObjects)
        {
            escapedObjectsMap.put(transferID, escapedObjects);
        }

        public IdentitySet<InstanceKey> getEscapedObjects(int transferID)
        {
            return escapedObjectsMap.get(transferID);
        }

        public void setTransferSafety(int transferID, boolean isSafeTransfer)
        {
            if (isSafeTransfer) {
                safeTransfers.add(transferID);
                unsafeTransfers.remove(transferID);
            } else {
                safeTransfers.remove(transferID);
                unsafeTransfers.add(transferID);
            }
        }

        public boolean isSafeTransfer(int transferID)
        {
            return safeTransfers.contains(transferID);
        }

        public boolean isUnsafeTransfer(int transferID)
        {
            return unsafeTransfers.contains(transferID);
        }

        public IntSet getUnsafeTransfers()
        {
            return unsafeTransfers;
        }

        public IntSet getSafeTransfers()
        {
            return safeTransfers;
        }

        public boolean hasUnsafeTransfers()
        {
            return !unsafeTransfers.isEmpty();
        }
    }


    /**
     * Visits the results of a (performed) `SoterAnalysis` and creates JSON to describe those
     * results.
     */
    private static class JsonCreator
    {
        private final static JsonGeneratorFactory jsonGeneratorFactory;

        static {
            Map<String, ?> JSON_GENERATOR_PROPERTIES = new HashMap<String, Void>();
            JSON_GENERATOR_PROPERTIES.put(JsonGenerator.PRETTY_PRINTING, null);
            jsonGeneratorFactory = Json.createGeneratorFactory(JSON_GENERATOR_PROPERTIES);
        }

        private final SoterAnalysis analysis;

        // Cache the created JSON artifacts; lazily evaluate them.
        private JsonObject json;
        private String jsonString;

        private JsonCreator(SoterAnalysis analysis)
        {
            assert analysis != null;
            this.analysis = analysis;
        }

        private String toJsonString()
        {
            assert analysis.hasBeenPerformed;

            if (jsonString != null) {
                return jsonString;
            }
            if (json == null) {
                toJson();  // Caches the result in `json`.
                assert json != null;
            }

            StringWriter writer = new StringWriter();
            JsonGenerator generator = jsonGeneratorFactory.createGenerator(writer);
            generator.writeStartArray();
            generator.write(json);
            generator.writeEnd();
            generator.close();
            return writer.toString();
        }

        private JsonObject toJson()
        {
            assert analysis.hasBeenPerformed;

            if (json != null) {
                return json;
            }

            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("capsuleTemplate", analysis.template.getQualifiedName());

            /*
            //JsonObjectBuilder tempObjectBuilder;
            JsonArrayBuilder methodsArrayBuilder = Json.createArrayBuilder();
            builder.add("methods", methodsArrayBuilder);

            // Make an array of all transfer sites.
            for (Entry<IMethod, IdentitySet<TransferSite>> entry : analysis.unsafeTransferSitesMap.entrySet())
            {
                IMethod method = entry.getKey();
                methodsArrayBuilder.add(method.getSignature());
                JsonObjectBuilder methodSummary

                // For each method, make an array of transfer sites.
                IdentitySet<TransferSite> unsafeTransferSites = entry.getValue();
                for (TransferSite unsafeTransferSite : unsafeTransferSites)
                {
                    reportWriter.append("    ");
                    jsonWriter.write(unsafeTransferSite.toJson());
                }
            }
            */

            // Make an array of all transfer sites.
            JsonArrayBuilder transferSitesBuilder = Json.createArrayBuilder();
            for (Entry<TransferSite, TransferSiteResults> entry : analysis.transferSiteResultsMap.entrySet())
            {
                transferSitesBuilder.add(toJson(entry.getKey(), entry.getValue()));
            }
            builder.add("transferSites", transferSitesBuilder);

            json = builder.build();
            return json;
        }

        private JsonObject toJson(TransferSite transferSite, TransferSiteResults results)
        {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("transferSite", transferSite.toJson())
                   .add("livePointerKeys", toJson(results.liveVariables))
                   .add("liveInstanceKeys", toJson(results.liveObjects));

            // Create an array of JSON objects describing the analysis results for each of the
            // current transfer site's transfers.
            IntIterator transfersIter = transferSite.getTransfers().intIterator();
            JsonArrayBuilder transfersArrayBuilder = Json.createArrayBuilder();
            while (transfersIter.hasNext())
            {
                int transfer = transfersIter.next();
                JsonObjectBuilder transferBuilder = Json.createObjectBuilder();
                transferBuilder.add("TransferID", transfer)
                               .add("escapedInstanceKeys", toJson(results.getEscapedObjects(transfer)))
                               .add("isSafeTransfer", results.isSafeTransfer(transfer));
                transfersArrayBuilder.add(transferBuilder);
            }
            builder.add("transfers", transfersArrayBuilder);
            return builder.build();
        }

        private JsonArray toJson(Set<PointerKey> set)
        {
            JsonArrayBuilder builder = Json.createArrayBuilder();
            for (PointerKey ptr : set) {
                builder.add(toJson(ptr));
            }
            return builder.build();
        }

        private JsonObject toJson(PointerKey ptr)
        {
            if (ptr instanceof InstanceFieldKey) return toJson((InstanceFieldKey) ptr);
            else if (ptr instanceof LocalPointerKey) return toJson((LocalPointerKey) ptr);
            else if (ptr instanceof StaticFieldKey) return toJson((StaticFieldKey) ptr);
            else if (ptr instanceof ArrayContentsKey) return toJson((ArrayContentsKey) ptr);
            else throw new RuntimeException("Found `PointerKey` which can't be handled: " + ptr);
        }

        private JsonObject toJson(InstanceFieldKey ptr)
        {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("type", "InstanceFieldKey");
            builder.add("field", ptr.getField().toString());
            return builder.build();
        }

        private JsonObject toJson(LocalPointerKey ptr)
        {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("type", "LocalPointerKey");
            builder.add("method", ptr.getNode().getMethod().getSignature());
            builder.add("valueNumber", ptr.getValueNumber());
            return builder.build();
        }

        private JsonObject toJson(StaticFieldKey ptr)
        {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("type", "StaticFieldKey");
            builder.add("field", ptr.getField().toString());
            return builder.build();
        }

        private JsonObject toJson(ArrayContentsKey ptr)
        {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("type", "ArrayContentsKey");
            return builder.build();
        }

        private JsonArray toJson(IdentitySet<InstanceKey> set)
        {
            JsonArrayBuilder builder = Json.createArrayBuilder();
            for (InstanceKey inst : set) {
                builder.add(toJson(inst));
            }
            return builder.build();
        }

        private JsonObject toJson(InstanceKey inst)
        {
            Iterator<Pair<CGNode, NewSiteReference>> iter = inst.getCreationSites(analysis.getCallGraph());

            // Assume that the returned iterator has exactly one `next()`.
            assert iter.hasNext() == true;
            Pair<CGNode, NewSiteReference> creationSite = iter.next();
            assert iter.hasNext() == false;

            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("type", "InstanceKey");
            builder.add("concreteType", inst.getConcreteType().toString());
            builder.add("creationSiteMethod", creationSite.fst.getMethod().getSignature());
            builder.add("creationSiteProgramCounter", creationSite.snd.getProgramCounter());
            builder.add("context", creationSite.fst.getContext().toString());
            return builder.build();
        }
    }
}
