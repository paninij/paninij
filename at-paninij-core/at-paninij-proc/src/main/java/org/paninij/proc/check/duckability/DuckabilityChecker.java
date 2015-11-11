package org.paninij.proc.check.duckability;

import static java.text.MessageFormat.format;

import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.proc.check.CheckEnvironment;
import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;

public class DuckabilityChecker
{
    private static final String ERROR_SOURCE = DuckabilityChecker.class.getName();
    
    private final CheckEnvironment env;
    private final DuckabilityCheck[] checks;
    
    public DuckabilityChecker(CheckEnvironment env) {
        this.env = env;
        this.checks = new DuckabilityCheck[] {
            new FieldModifiersCheck(),
            new MethodModifiersCheck(),
        };
    }
    
    /**
     * @param toDuck  A type element representing some type to be ducked.
     * @return  The result of the duckability check.
     */
    public Result check(TypeMirror toDuck)
    {
        switch (toDuck.getKind()) {
        case VOID:
            return ok;
        case DECLARED:
            return checkDeclared(toDuck);
        default:
            String err = "Cannot duck type `{0}` because it has type kind {1}";
            err = format(err, toDuck, toDuck.getKind());
            return new Error(err, ERROR_SOURCE);
        }
    }
    
    public Result checkDeclared(TypeMirror toDuck)
    {
        TypeElement toDuckElem = (TypeElement) env.getTypeUtils().asElement(toDuck);
        if (toDuckElem == null) {
            throw new RuntimeException("Could not convert type mirror to type element: " + toDuck);
        }

        for (DuckabilityCheck check : checks) {
            Result result = check.checkDuckability(toDuckElem);
            if (! result.ok()) {
                return result;
            }
        }
        
        return ok; 
    }
}
