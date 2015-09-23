package org.paninij.soter.util;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.paninij.runtime.util.IdentitySet;
import org.paninij.soter.site.AnalysisSite;

import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Context;
import com.ibm.wala.ipa.callgraph.propagation.ArrayContentsKey;
import com.ibm.wala.ipa.callgraph.propagation.InstanceFieldKey;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.LocalPointerKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.callgraph.propagation.ReceiverInstanceContext;
import com.ibm.wala.ipa.callgraph.propagation.StaticFieldKey;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.intset.OrdinalSetMapping;


/**
 * Visits the results of a (performed) `SoterAnalysis` and creates JSON to describe those
 * results. Note that the analysis need not be performed before construction of the creator; the
 * analysis only needs to be performed before calling `toJson()` for the first time.
 */
public abstract class AnalysisJsonResultsCreator
{
    private final static JsonGeneratorFactory jsonGeneratorFactory;

    static {
        Map<String, ?> JSON_GENERATOR_PROPERTIES = new HashMap<String, Void>();
        JSON_GENERATOR_PROPERTIES.put(JsonGenerator.PRETTY_PRINTING, null);
        jsonGeneratorFactory = Json.createGeneratorFactory(JSON_GENERATOR_PROPERTIES);
    }

    // Cache the created JSON artifacts; lazily evaluate them.
    protected JsonObject json;
    protected String jsonString;

    public abstract JsonObject toJson();
    
    
    // TODO: Refactor to remove this.
    public abstract CallGraph getCallGraph();


    public String toJsonString()
    {
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

    protected JsonArray toJson(Set<PointerKey> set)
    {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (PointerKey ptr : set) {
            builder.add(toJson(ptr));
        }
        return builder.build();
    }

    protected JsonObject toJson(PointerKey ptr)
    {
        if (ptr instanceof InstanceFieldKey) return toJson((InstanceFieldKey) ptr);
        else if (ptr instanceof LocalPointerKey) return toJson((LocalPointerKey) ptr);
        else if (ptr instanceof StaticFieldKey) return toJson((StaticFieldKey) ptr);
        else if (ptr instanceof ArrayContentsKey) return toJson((ArrayContentsKey) ptr);
        else throw new RuntimeException("Found `PointerKey` which can't be handled: " + ptr);
    }

    protected JsonObject toJson(InstanceFieldKey ptr)
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", "InstanceFieldKey");
        builder.add("field", ptr.getField().toString());
        return builder.build();
    }

    protected JsonObject toJson(LocalPointerKey ptr)
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", "LocalPointerKey");
        builder.add("method", ptr.getNode().getMethod().getSignature());
        builder.add("valueNumber", ptr.getValueNumber());
        return builder.build();
    }

    protected JsonObject toJson(StaticFieldKey ptr)
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", "StaticFieldKey");
        builder.add("field", ptr.getField().toString());
        return builder.build();
    }

    protected JsonObject toJson(ArrayContentsKey ptr)
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", "ArrayContentsKey");
        return builder.build();
    }

    protected JsonArray instanceKeysToJson(Set<InstanceKey> set)
    {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (InstanceKey inst : set) {
            builder.add(toJson(inst));
        }
        return builder.build();
    }

    protected JsonObject toJson(InstanceKey inst)
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", "InstanceKey");
        builder.add("concreteType", inst.getConcreteType().toString());
        builder.add("creationSites", toJsonBuilder(inst.getCreationSites(this.getCallGraph())));
        return builder.build();
    }
    
    protected JsonArrayBuilder toJsonBuilder(Iterator<Pair<CGNode, NewSiteReference>> creationSites)
    {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        while (creationSites.hasNext()) {
            builder.add(toJsonBuilder(creationSites.next()));
        }
        return builder;
    }
    
    protected JsonObjectBuilder toJsonBuilder(Pair<CGNode, NewSiteReference> creationSite)
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("creationSiteMethod", creationSite.fst.getMethod().getSignature());
        builder.add("creationSiteProgramCounter", creationSite.snd.getProgramCounter());
        builder.add("context", creationSite.fst.getContext().toString());
        return builder;
    }
    
    protected JsonObjectBuilder toJsonBuilder(CGNode node)
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("method", node.getMethod().getSignature());
        builder.add("context", toJsonBuilder(node.getContext()));
        return builder;
    }
    
    protected JsonObjectBuilder toJsonBuilder(Context context)
    {
        if (context instanceof ReceiverInstanceContext) {
            return toJson((ReceiverInstanceContext) context);
        } else {
            // TODO: Fix this ugliness!
            return Json.createObjectBuilder().addNull(context.toString());
        }
    }
    
    protected JsonObjectBuilder toJson(ReceiverInstanceContext context)
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("receiverType", context.getReceiver().getConcreteType().getName().toString());
        builder.add("receiverCreationSites", toJsonBuilder(context.getReceiver()
                                                             .getCreationSites(getCallGraph())));
        return builder;
    }
    
    protected <T extends AnalysisSite> JsonObjectBuilder toJsonBuilder(CGNode node, Set<T> sites)
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("node", toJsonBuilder(node));
        
        JsonArrayBuilder sitesArrayBuilder = Json.createArrayBuilder();
        for (AnalysisSite site: sites) {
            sitesArrayBuilder.add(site.toJson());
        }
        builder.add("sites", sitesArrayBuilder);
        return builder;
    }
    
    
    protected <T> JsonArrayBuilder toJsonBuilder(Iterable<T> set)
    {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (T elem: set) {
            builder.add(elem.toString());
        }
        return builder;
    }
    
    protected <T extends AnalysisSite> JsonArrayBuilder toJsonBuilder(Map<CGNode, Set<T>> map)
    {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Entry<CGNode, Set<T>> entry: map.entrySet())
        {
            builder.add(toJsonBuilder(entry.getKey(), entry.getValue()));
        } 
        return builder;
    }
    
    protected <T extends AnalysisSite> JsonArrayBuilder ptrMapToJsonBuilder(Map<T, Set<PointerKey>> map)
    {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Entry<T, Set<PointerKey>> entry: map.entrySet())
        {
            builder.add(toJsonBuilder(entry.getKey(), entry.getValue()));
        } 
        return builder;
    }
    
    protected JsonObjectBuilder toJsonBuilder(AnalysisSite site, Set<PointerKey> ptrs)
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("site", site.toJson());
        
        JsonArrayBuilder sitesArrayBuilder = Json.createArrayBuilder();
        for (PointerKey ptr: ptrs) {
            sitesArrayBuilder.add(toJson(ptr));
        }
        builder.add("pointerKeys", sitesArrayBuilder);
        return builder;
    }
    
    protected JsonObjectBuilder toJsonBuilder(OrdinalSetMapping<PointerKey> mapping)
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        int maxIdx = mapping.getMaximumIndex();
        for (int idx = 0; idx <= maxIdx; idx++) {
            builder.add(Integer.toString(idx), toJson(mapping.getMappedObject(idx)));
        }

        return builder;
    }
}