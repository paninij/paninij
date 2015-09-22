package org.paninij.soter.instrument;

import org.paninij.soter.model.CapsuleTemplate;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.ShrikeClass;
import com.ibm.wala.shrikeBT.analysis.ClassHierarchyStore;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;

public class TemplateInstrumenterFactory
{
    protected final String outputDir;
    
    public TemplateInstrumenterFactory(String outputDir)
    {
        this.outputDir = outputDir;
    }

    public ClassInstrumenter make(CapsuleTemplate template)
    {
        IClass templateClass = template.getTemplateClass();
        if (templateClass instanceof ShrikeClass == false) {
            String msg = "Could not cast the template's `IClass` to a `ShrikeClass`";
            throw new IllegalArgumentException(msg);
        }
        ShrikeClass templateShrike = (ShrikeClass) templateClass;
        
        try
        {
            return new ClassInstrumenter(template.getWalaPath(), templateShrike.getReader(),
                                         new ClassHierarchyStore(), false);
        }
        catch (InvalidClassFileException ex)
        {
            String msg = "Failed to make a `SoterInstrumenter`, because failed to make a needed "
                       + "`ClassInstrumenter` instance: " + ex;
            throw new IllegalArgumentException(msg);
        }
    }
}
