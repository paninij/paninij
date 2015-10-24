package org.paninij.proc.check;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.paninij.proc.driver.ProcDriver.makeDefaultSettings;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import org.paninij.proc.driver.ProcDriver;

public abstract class AbstractTestBadTemplates
{
    private final ProcDriver driver;
    
    public AbstractTestBadTemplates()
    {
        try {
            driver = new ProcDriver(makeDefaultSettings());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Before
    public void setUp()
    {
        expected.expect(RuntimeException.class);
        expected.expectCause(instanceOf(getExpectedCause()));
        // TODO: Figure out if we can specify the expected error source (i.e. the check from which
        // the exception was originally thrown).
    }
    
    protected void testBadTemplate(String badTemplate)
    {
        try {
            driver.process(getBadTemplatePackage() + "." + badTemplate);
        } catch (RuntimeException ex) {
            // TODO: Log error messages from checks better.
            //System.err.println(ex.getMessage());  // For logging check messages
            throw ex;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected abstract String getBadTemplatePackage();
    
    protected abstract Class<?> getExpectedCause();
}
