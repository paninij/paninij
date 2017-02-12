/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
package org.paninij.soter.instrument;

import static java.io.File.separator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.paninij.runtime.check.DynamicOwnershipTransfer;
import org.paninij.runtime.util.IdentitySet;
import org.paninij.soter.SoterAnalysis;
import org.paninij.soter.model.CapsuleCore;
import org.paninij.soter.transfer.TransferSite;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeClass;
import com.ibm.wala.shrikeBT.DupInstruction;
import com.ibm.wala.shrikeBT.InvokeInstruction;
import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.Util;
import com.ibm.wala.shrikeBT.MethodEditor.Output;
import com.ibm.wala.shrikeBT.MethodEditor.Patch;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter.MethodExaminer;
import com.ibm.wala.shrikeCT.ClassWriter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;

public class SoterInstrumenter
{
    protected final CapsuleCore core;
    protected final SoterAnalysis sa;
    protected final String outputDir;
    protected final ClassInstrumenter instrumenter;  // Instrumenter of the capsule core class.
    
    protected final String outputFilePath;
    protected final ShrikeClass shrikeClass;
    protected final Map<String, IdentitySet<TransferSite>> unsafeTransferSitesMap;
    protected final MethodInstrumenter methodInstrumenter;

    public SoterInstrumenter(CapsuleCore core, String outputDir, SoterAnalysis sa,
                             ClassInstrumenter instrumenter) throws InvalidClassFileException
    {
        this.core = core;
        this.sa = sa;
        this.outputDir = outputDir;
        this.instrumenter = instrumenter;

        outputFilePath = outputDir + separator + instrumenter.getReader().getName() + ".class";
        shrikeClass = (ShrikeClass) core.getCoreClass();
        unsafeTransferSitesMap = new HashMap<String, IdentitySet<TransferSite>>();
        methodInstrumenter = new MethodInstrumenter();
    }

    public void perform()
    {
        try
        {
            buildUnsafeTransferSitesMap();
            if (unsafeTransferSitesMap.isEmpty()) {
                return;  // Return if there are no transfer sites that need instrumentation.
            }
            
            instrumenter.visitMethods(methodInstrumenter);
            writeInstrumentedClassFile();
        }
        catch (InvalidClassFileException | IOException ex) {
            throw new RuntimeException("Failed to perform soter instrumentation: " + ex, ex);
        }
    }
    
    protected void buildUnsafeTransferSitesMap()
    {
        for (Entry<IMethod, IdentitySet<TransferSite>> entry : sa.getUnsafeTransferSitesMap().entrySet())
        {
            String signature = "L" + entry.getKey().getSignature();
            unsafeTransferSitesMap.put(signature, entry.getValue());
        }
    }
    
    protected void writeInstrumentedClassFile() throws InvalidClassFileException, IOException
    {
        ClassWriter classWriter = instrumenter.emitClass();
        FileOutputStream outputStream = new FileOutputStream(outputFilePath);
        outputStream.write(classWriter.makeBytes());
        outputStream.flush();
        outputStream.close();
    }
    
    
    private class MethodInstrumenter implements MethodExaminer
    {
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
            IdentitySet<TransferSite> unsafeTransferSites = unsafeTransferSitesMap.get(signature);

            // Ignore any methods on the core in which there are no unsafe transfer sites.
            if (unsafeTransferSites == null || unsafeTransferSites.isEmpty()) {
                return;
            }
            instrumentUnsafeTransferSites(methodData, unsafeTransferSites);
        }
        
        private void instrumentUnsafeTransferSites(MethodData methodData,
                                                   IdentitySet<TransferSite> unsafeTransferSites)
        {
            MethodEditor methodEditor = new MethodEditor(methodData);
            for (TransferSite site : unsafeTransferSites) {
                patchUnsafeTransferSite(methodEditor, site);
            }
            methodEditor.applyPatches();
            methodEditor.endPass();
        }
        
        private void patchUnsafeTransferSite(MethodEditor methodEditor, TransferSite site)
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
}
