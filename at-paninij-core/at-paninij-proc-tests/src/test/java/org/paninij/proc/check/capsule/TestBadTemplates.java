package org.paninij.proc.check.capsule;

import org.junit.Test;
import org.paninij.proc.check.AbstractTestBadTemplates;
import org.paninij.proc.check.capsule.CapsuleCheckException;


public class TestBadTemplates extends AbstractTestBadTemplates
{
    @Override
    protected String getBadTemplatePackage() {
        return "org.paninij.proc.check.capsule";
    }

    @Override
    protected Class<?> getExpectedCause() {
        return CapsuleCheckException.class;
    }
    
    @Test
    public void testNotSubclassCheck() {
        testBadTemplate("IsSubclassTemplate");
    }
    
    @Test
    public void testNoVarargsMethodsCheck() {
        testBadTemplate("HasVarargsMethodTemplate");
    }

    @Test
    public void testOnlyZeroArgConstructorsCheck() {
        testBadTemplate("HasIllegalConstructorTemplate");
    }

    @Test
    public void testSuffixCheck() {
        testBadTemplate("BadSuffix");
    }

    @Test
    public void testInitDeclCheck1() {
        testBadTemplate("decls.BadParamsInitTemplate");
    }

    @Test
    public void testInitDeclCheck2() {
        testBadTemplate("decls.NonVoidInitTemplate");
    }

    @Test
    public void testInitDeclCheck3() {
        testBadTemplate("decls.TypeParamsInitTemplate");
    }
    
    @Test
    public void testRunDeclCheck1() {
        testBadTemplate("decls.BadParamsRunTemplate");
    }

    @Test
    public void testRunDeclCheck2() {
        testBadTemplate("decls.NonVoidRunTemplate");
    }

    @Test
    public void testRunDeclCheck3() {
        testBadTemplate("decls.TypeParamsRunTemplate");
    }
    
    @Test
    public void testNoNestedTypesCheck1() {
        testBadTemplate("nested.NestedAnnotationTemplate");
    }
    
    @Test
    public void testNoNestedTypesCheck2() {
        testBadTemplate("nested.NestedClassTemplate");
    }
    
    @Test
    public void testNoNestedTypesCheck3() {
        testBadTemplate("nested.NestedEnumTemplate");
    }
    
    @Test
    public void testNoNestedTypesCheck4() {
        testBadTemplate("nested.NestedInterfaceTemplate");
    }
    
    @Test
    public void testNoTypeParamsCheck() {
        testBadTemplate("HasTypeParamTemplate");
    }
    
    @Test
    public void testNoIllegalModifiersCheck1() {
        testBadTemplate("modifiers.AbstractTemplate");
    }

    @Test
    public void testNoIllegalModifiersCheck2() {
        testBadTemplate("modifiers.PrivateConstructorTemplate");
    } 

    @Test
    public void testNoIllegalModifiersCheck3() {
        testBadTemplate("modifiers.SynchronizedMethodTemplate");
    } 

    @Test
    public void testNoIllegalModifiersCheck4() {
        testBadTemplate("modifiers.VolatileFieldTemplate");
    } 
    
    @Test
    public void testNoIllegalModifiersCheck5() {
        testBadTemplate("modifiers.PrivateInitTemplate");
    }

    @Test
    public void testNoIllegalModifiersCheck6() {
        testBadTemplate("modifiers.StaticInitTemplate");
    }

    @Test
    public void testNoIllegalModifiersCheck7() {
        testBadTemplate("modifiers.PrivateFieldTemplate");
    } 

    @Test
    public void testProceduresCheck1() {
        testBadTemplate("procedures.ProcedureOnActiveTemplate");
    }

    @Test
    public void testProceduresCheck2() {
        testBadTemplate("procedures.NonPublicProcedureTemplate");
    }
    
    @Test
    public void testNotTooManyDeclsCheck1() {
        testBadTemplate("toomany.TooManyInitDeclsTemplate");
    }
    
    @Test
    public void testNotTooManyDeclsCheck2() {
        testBadTemplate("toomany.TooManyRunDeclsTemplate");
    }
    
    @Test
    public void testNotTooManyDeclsCheck3() {
        testBadTemplate("toomany.TooManyDesignDeclsTemplate");
    }
    
    @Test
    public void testFieldsCheck1() {
        testBadTemplate("fields.BothAnnotationsTemplate");
    }

    @Test
    public void testFieldsCheck2() {
        testBadTemplate("fields.LocalStateTemplate");
    }

    @Test
    public void testFieldsCheck3() {
        testBadTemplate("fields.MissingAnnotationTemplate");
    }
    
    @Test
    public void testFieldsCheck4() {
        testBadTemplate("fields.TemplateFieldTemplate");
    }

    @Test
    public void testFieldsCheck5() {
        testBadTemplate("fields.TwoDimensionalArrayTemplate");
    }
    
    @Test
    public void testImplementedInterfaces1() {
        testBadTemplate("implemented.ImplementsSignatureTemplate");
    }
    
    @Test
    public void testImplementedInterfaces2() {
        testBadTemplate("implemented.ImplementsNormalTemplate");
    }
    
    @Test
    public void testDuckability1() {
        testBadTemplate("duckability.kind.ArrayKindTemplate");
    }
    
    @Test
    public void testDuckability2() {
        testBadTemplate("duckability.kind.PrimitiveKindTemplate");
    }

    @Test
    public void testDuckability3() {
        testBadTemplate("duckability.ReturnTypeHasFinalMethodTemplate");
    }

    @Test
    public void testDuckability4() {
        testBadTemplate("duckability.ReturnTypeIsFinalTemplate");
    }

    @Test
    public void testDuckability5() {
        testBadTemplate("duckability.ReturnTypeHasExposedFieldsTemplate");
    }
    
    @Test
    public void testNoImportedFieldsOnRootCheck() {
        testBadTemplate("HasImportedFieldsOnRootTemplate");
    }
    
    @Test
    public void testNoBadMethodNamesCheck() {
        testBadTemplate("HasBadMethodNameTemplate");
    }
    
    @Test
    public void testNoDefaultPackageCheck() {
    	testBadTemplate("CapsuleInDefaultTemplate", "");
    }
}
