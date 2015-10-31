package org.paninij.proc.driver;

import static org.paninij.proc.driver.ProcDriver.makeDefaultSettings;

import java.io.IOException;

import org.junit.Test;
import org.paninij.proc.driver.ProcDriver;

/**
 * Includes tests which perform a Java compilation task with a `PaniniProcessor` instance configured
 * to use `-Apanini.staticOwnership=SOTER`.
 */
public class TestProcDriver
{
    private final ProcDriver driver;
    
    public TestProcDriver() throws IOException {
        driver = new ProcDriver(makeDefaultSettings());
    }
    
    @Test
    public void processNormalTemplate() throws IOException {
        driver.process("org.paninij.proc.shapes.NormalTemplate");
    }
}
