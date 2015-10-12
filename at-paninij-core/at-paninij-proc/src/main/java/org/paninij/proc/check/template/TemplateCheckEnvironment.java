package org.paninij.proc.check.template;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class TemplateCheckEnvironment
{
    private final ProcessingEnvironment procEnv;
    
    public TemplateCheckEnvironment(ProcessingEnvironment procEnv) {
        this.procEnv = procEnv;
    }
    
    public Types getTypeUtils() {
        return procEnv.getTypeUtils();
    }
    
    public Elements getElementUtils() {
        return procEnv.getElementUtils();
    }
}
