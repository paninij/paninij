package org.paninij.proc.check.signature;

import org.junit.Test;
import org.paninij.proc.check.AbstractTestBadTemplates;


public class TestBadTemplates extends AbstractTestBadTemplates
{
    @Override
    protected String getBadTemplatePackage() {
        return "org.paninij.proc.check.signature";
    }

    @Override
    protected Class<?> getExpectedCause() {
        return SignatureCheckException.class;
    }
    
    @Test
    public void testBadSuffix() {
        testBadTemplate("BadSuffix");
    }
    
    @Test
    public void testBadModifier() {
        testBadTemplate("BadModifierTemplate");
    }
    
    @Test
    public void testBadName() {
        testBadTemplate("BadNameTemplate");
    }
    
    @Test
    public void testIsSubinterface() {
        testBadTemplate("IsSubinterfaceTemplate");
    }
    
    @Test
    public void testHasTypeParamTemplate() {
        testBadTemplate("HasTypeParamTemplate");
    }
    
    @Test
    public void testHasNestedTypeTemplate() {
        testBadTemplate("HasNestedTypeTemplate");
    }
    
    @Test
    public void testHasRootAnnotationTemplate() {
        testBadTemplate("HasRootAnnotationTemplate");
    }
    
    @Test
    public void testNoDefaultPackageCheck() {
    	testBadTemplate("SignatureInDefaultTemplate", "");
    }
}
