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
    public void testNoMainCheck() {
        testBadTemplate("HasMainTemplate");
    }
    
    @Test
    public void testInitDeclCheck0() {
        testBadTemplate("init.ParamsInitTemplate");
    }

    @Test
    public void testInitDeclCheck1() {
        testBadTemplate("init.NonVoidInitTemplate");
    }
    
    @Test
    public void testInitDeclCheck2() {
        testBadTemplate("init.PrivateInitTemplate");
    }

    @Test
    public void testInitDeclCheck3() {
        testBadTemplate("init.StaticInitTemplate");
    }

    @Test
    public void testInitDeclCheck4() {
        testBadTemplate("init.TooManyInitDeclsTemplate");
    }

    @Test
    public void testInitDeclCheck5() {
        testBadTemplate("init.TypeParamsInitTemplate");
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
    
    private void testBadTemplate(String badTemplate)
    {
        try {
            driver.process(badTemplatePackage + "." + badTemplate);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
