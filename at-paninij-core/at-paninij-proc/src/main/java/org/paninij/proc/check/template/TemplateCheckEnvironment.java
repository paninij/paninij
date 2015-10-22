package org.paninij.proc.check.template;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class TemplateCheckEnvironment
{
    private final ProcessingEnvironment procEnv;
    private final RoundEnvironment roundEnv;
    
    public TemplateCheckEnvironment(ProcessingEnvironment procEnv, RoundEnvironment roundEnv)
    {
        this.procEnv = procEnv;
        this.roundEnv = roundEnv;
    }
    
    public Types getTypeUtils() {
        return procEnv.getTypeUtils();
    }
    
    public Elements getElementUtils() {
        return procEnv.getElementUtils();
    }
}
