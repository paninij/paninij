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
    public void testInitDeclCheck() {
        testBadTemplate("init.NonVoidInitTemplate");
        testBadTemplate("init.PrivateInitTemplate");
        testBadTemplate("init.ProtectedInitTemplate");
        testBadTemplate("init.StaticInitTemplate");
        testBadTemplate("init.TooManyInitDeclsTemplate");
        testBadTemplate("init.TypeParamsInitTemplate");
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
