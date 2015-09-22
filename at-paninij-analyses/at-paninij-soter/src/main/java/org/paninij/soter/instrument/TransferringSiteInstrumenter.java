package org.paninij.soter.instrument;

import java.util.Map;
import java.util.Set;

import org.paninij.runtime.check.DynamicOwnershipTransfer;
import org.paninij.soter.site.TransferringSite;

import com.ibm.wala.shrikeBT.DupInstruction;
import com.ibm.wala.shrikeBT.InvokeInstruction;
import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.Util;
import com.ibm.wala.shrikeBT.MethodEditor.Output;
import com.ibm.wala.shrikeBT.MethodEditor.Patch;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter.MethodExaminer;

class TransferringSiteInstrumenter implements MethodExaminer
{
    private static final DupInstruction DUP_INSTR = DupInstruction.make(0);
    private static final InvokeInstruction ASSERT_SAFE_TRANFER_INSTR
                           = Util.makeInvoke(DynamicOwnershipTransfer.class, "assertSafeTransfer");

    private final Map<String, Set<TransferringSite>> sitesToInstrument;

    /**
     * @param sitesToInstrument A map where each key is expected to be a method signature produced
     *                          by `IMethod.getSignature()`. The value associated with each such key
     *                          is expected to be a set of `TransferringSites` defined within that
     *                          method.
     */
    TransferringSiteInstrumenter(Map<String, Set<TransferringSite>> sitesToInstrument)
    {
        this.sitesToInstrument = sitesToInstrument;
    }

    @Override
    public void examineCode(MethodData methodData)
    {
        // Note that this name mangling is performed because of the differences between the 
        // `getSignature()` methods on `MethodData` and `IMethod`.
        // TODO: Make name mangling more consistent so that it doesn't need to be done both here and
        // by clients.
        String signature = methodData.getClassType().replace('/', '.').replace(';', '.')
                         + methodData.getName() + methodData.getSignature();
        Set<TransferringSite> sites = sitesToInstrument.get(signature);

        // Ignore any methods on the template in which there are no unsafe transfer sites.
        if (sites == null || sites.isEmpty()) {
            return;
        }
        instrumentAllSitesWithinMethod(sites, methodData);
    }
    
    private void instrumentAllSitesWithinMethod(Set<TransferringSite> sites, MethodData methodData)
    {
        MethodEditor methodEditor = new MethodEditor(methodData);
        methodEditor.beginPass();
        for (TransferringSite site : sites) {
            patchSiteWithinMethod(site, methodEditor);
        }
        methodEditor.applyPatches();
        methodEditor.endPass();
    }
    
    /**
     * Assumes that the `beginPass()` has been called on the given `methodEditor`.
     */
    private void patchSiteWithinMethod(TransferringSite site, MethodEditor methodEditor)
    {
        methodEditor.insertBefore(site.getInstruction().iindex, new Patch()
        {
            @Override
            public void emitTo(Output w)
            {
                w.emit(DUP_INSTR);
                w.emit(ASSERT_SAFE_TRANFER_INSTR);
            }
        });
    }
}