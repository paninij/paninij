package org.paninij.proc.check.template;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.paninij.proc.driver.ProcDriver.makeDefaultSettings;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.paninij.proc.driver.ProcDriver;

public class TestBadTemplates
{
    private static final String badTemplatePackage = "org.paninij.proc.check.template";
    
    private final ProcDriver driver;
    
    public TestBadTemplates() throws IOException {
        driver = new ProcDriver(makeDefaultSettings());
    }
    
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Before
    public void setUp()
    {
        expected.expect(RuntimeException.class);
        expected.expectCause(instanceOf(TemplateCheckException.class));
        // TODO: Figure out if we can specify the expected error source (i.e. the check from which
        // the exception was originally thrown).
    }
    
    @Test
    public void testNotSubclassCheck() {
        testBadTemplate("IsSubclassTemplate");
    }
    
    @Test
    public void testNoVariadicMethodsCheck() {
        testBadTemplate("HasVariadicMethodTemplate");
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

    private void testBadTemplate(String badTemplate)
    {
        try {
            driver.process(badTemplatePackage + "." + badTemplate);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
