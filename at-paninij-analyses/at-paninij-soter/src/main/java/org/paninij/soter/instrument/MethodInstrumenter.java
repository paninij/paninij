package org.paninij.soter.instrument;

import org.paninij.runtime.check.DynamicOwnershipTransfer;
import org.paninij.runtime.util.IdentitySet;
import org.paninij.soter.site.TransferringSite;

import com.ibm.wala.shrikeBT.DupInstruction;
import com.ibm.wala.shrikeBT.InvokeInstruction;
import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.Util;
import com.ibm.wala.shrikeBT.MethodEditor.Output;
import com.ibm.wala.shrikeBT.MethodEditor.Patch;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter.MethodExaminer;

class MethodInstrumenter implements MethodExaminer
{
    /**
     * 
     */
    private final SoterInstrumenter soterInstrumenter;

    /**
     * @param soterInstrumenter
     */
    MethodInstrumenter(SoterInstrumenter soterInstrumenter)
    {
        this.soterInstrumenter = soterInstrumenter;
    }

    final DupInstruction dup = DupInstruction.make(0);
    final InvokeInstruction assertSafeTransfer = Util.makeInvoke(DynamicOwnershipTransfer.class,
                                                                 "assertSafeTransfer");
    
    @Override
    public void examineCode(MethodData methodData)
    {
        // Note that this name mangling is performed because the differences between the 
        // `getSignature()` methods on `MethodData` and `IMethod`.
        String signature = methodData.getClassType().replace('/', '.').replace(';', '.')
                         + methodData.getName()
                         + methodData.getSignature();
        IdentitySet<TransferringSite> unsafeTransferSites = this.soterInstrumenter.unsafeTransferSitesMap.get(signature);

        // Ignore any methods on the template in which there are no unsafe transfer sites.
        if (unsafeTransferSites == null || unsafeTransferSites.isEmpty()) {
            return;
        }
        instrumentUnsafeTransferSites(methodData, unsafeTransferSites);
    }
    
    private void instrumentUnsafeTransferSites(MethodData methodData,
                                               IdentitySet<TransferringSite> unsafeTransferSites)
    {
        MethodEditor methodEditor = new MethodEditor(methodData);
        for (TransferringSite site : unsafeTransferSites) {
            patchUnsafeTransferSite(methodEditor, site);
        }
        methodEditor.applyPatches();
        methodEditor.endPass();
    }
    
    private void patchUnsafeTransferSite(MethodEditor methodEditor, TransferringSite site)
    {
        methodEditor.beginPass();
        methodEditor.insertBefore(site.getInstruction().iindex, new Patch()
        {
            @Override
            public void emitTo(Output w)
            {
                w.emit(dup);
                w.emit(assertSafeTransfer);
            }
        });
    }
}