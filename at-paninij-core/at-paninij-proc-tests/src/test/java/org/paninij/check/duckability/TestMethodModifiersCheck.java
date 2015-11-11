package org.paninij.check.duckability;

import static org.junit.Assert.assertFalse;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.paninij.proc.check.Result;
import org.paninij.proc.check.duckability.MethodModifiersCheck;

import com.google.testing.compile.CompilationRule;

public class TestMethodModifiersCheck
{
    public @Rule CompilationRule rule = new CompilationRule();
    private Elements elements;
    private Types types;
    private MethodModifiersCheck check;

    @Before
    public void setup() {
        elements = rule.getElements();
        types = rule.getTypes();
        check = new MethodModifiersCheck();
    }

    @Test
    public void testCheckForProblematicProtectedMethod()
    {
        // Check should fail because `Socket` is in a protected package and at least one method,
        // `Socket.createImpl()`, is package-private.
        TypeElement socket = elements.getTypeElement("java.net.Socket");
        Result result = check.checkForProblematicProtectedMethod(socket);
        assertFalse(result.ok());
    }
    
    @Test
    public void testCheckForIllegalFinalMethod() {
        // This should fail because 
        TypeElement socket = elements.getTypeElement("java.net.Socket");
        Result result = check.checkForIllegalFinalMethod(socket);
        assertFalse(result.ok());
    }
}
