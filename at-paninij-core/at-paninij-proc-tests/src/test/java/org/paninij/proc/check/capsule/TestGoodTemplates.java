package org.paninij.proc.check.capsule;

import static org.paninij.proc.driver.ProcDriver.makeDefaultSettings;

import java.io.IOException;

import org.junit.Test;
import org.paninij.proc.driver.ProcDriver;

public class TestGoodTemplates
{
    private final ProcDriver driver;
    
    public TestGoodTemplates() throws IOException {
        driver = new ProcDriver(makeDefaultSettings());
    }
    
    @Test
    public void testMainGen()
    {
        testGoodTemplates("org.paninij.proc.activepassive", "XTemplate", "YTemplate", "ZTemplate");
    }

    private void testGoodTemplates(String pkg, String... templates)
    {
        if (templates.length == 0) {
            return;
        }
        if (pkg != null && !pkg.equals("")) {
            for (int idx = 0; idx < templates.length; idx++) {
                templates[idx] = pkg + "." + templates[idx];
            }
        }
        try {
            driver.process(templates);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
