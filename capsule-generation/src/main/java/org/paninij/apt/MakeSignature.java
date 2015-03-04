package org.paninij.apt;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.paninij.apt.util.Source;

public class MakeSignature {
    TypeElement template;
    PaniniPress context;
    
    static MakeSignature make(PaniniPress context, TypeElement template) {
        MakeSignature sig = new MakeSignature();
        sig.context = context;
        sig.template = template;
        return sig;
    }
    
    void makeSourceFile() {
        context.createJavaFile(buildQualifiedSignatureName(), buildSignature());
    }
    
    String buildSignature() {
        String pkg = buildPackage();
        String src = Source.lines(0, "package #0;",
                                     "",
                                     "#1",
                                     "",
                                     "/**",
                                     " * This signature was auto-generated from `#2`",
                                     " */",
                                     "#3",
                                     "{",
                                     "#4",
                                     "}");
        return Source.format(src, pkg,
                                  buildSignatureImports(),
                                  pkg + "." + template.getSimpleName(),
                                  buildSignatureDecl(),
                                  buildSignatureBody());
    }
    
    String buildPackage() {
        return context.getPackageOf(template);
    }
    
    String buildSignatureBody() {
        // TODO
        return "";
    }
    String buildSignatureImports() {
        // TODO
        return "";
    }
    
    String buildSignatureName() {
        return template.getSimpleName() + "$Signature";
    }
    
    String buildQualifiedSignatureName() {
        return template.getQualifiedName() + "$Signature";
    }
    
    String buildSignatureDecl() {
        return "public interface " + buildSignatureName();
    }
    
    String buildMethod(ExecutableElement method) {
        
        return "";
    }
}
