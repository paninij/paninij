package org.paninij.soter.instrument;

import org.paninij.soter.SoterAnalysis;
import org.paninij.soter.model.CapsuleTemplate;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.ShrikeClass;
import com.ibm.wala.shrikeBT.analysis.ClassHierarchyStore;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;

public class SoterInstrumenterFactory
{
    protected final String outputDir;
    
    public SoterInstrumenterFactory(String outputPath)
    {
        this.outputDir = outputPath;
    }

    public SoterInstrumenter make(SoterAnalysis sa)
    {
        CapsuleTemplate template = sa.getCapsuleTemplate();
        IClass templateClass = template.getTemplateClass();
        if (templateClass instanceof ShrikeClass == false) {
            String msg = "Could not cast the template's `IClass` to a `ShrikeClass`";
            throw new IllegalArgumentException(msg);
        }
        ShrikeClass templateShrike = (ShrikeClass) templateClass;
        
        try
        {
            ClassInstrumenter instrumenter = new ClassInstrumenter(template.getWalaPath(),
                                                                   templateShrike.getReader(),
                                                                   new ClassHierarchyStore(),
                                                                   false);
            return new SoterInstrumenter(template, outputDir, sa, instrumenter);
        }
        catch (InvalidClassFileException ex)
        {
            String msg = "Failed to make a `SoterInstrumenter`, because failed to make a needed "
                       + "`ClassInstrumenter` instance: " + ex;
            throw new IllegalArgumentException(msg);
        }
    }
}
