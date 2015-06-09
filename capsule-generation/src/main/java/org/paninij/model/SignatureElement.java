package org.paninij.model;

import java.util.ArrayList;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.paninij.apt.TemplateVisitor;

//TODO
public class SignatureElement implements Signature
{
    public static Signature make(TypeElement e) {
        SignatureElement signature = new SignatureElement();
        TemplateVisitor visitor = new TemplateVisitor();
//        e.accept(visitor,  signature);
        return signature;
    }

    @Override
    public String getSimpleName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getQualifiedName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ArrayList<Procedure> getProcedures() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setTypeElement(TypeElement e)
    {
        // TODO Auto-generated method stub

    }

    public void addExecutable(ExecutableElement e)
    {
        // TODO Auto-generated method stub

    }

}
