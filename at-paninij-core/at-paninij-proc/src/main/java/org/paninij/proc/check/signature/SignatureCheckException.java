package org.paninij.proc.check.signature;

public class SignatureCheckException extends RuntimeException
{
    private static final long serialVersionUID = 3022661365708183837L;
    
    public SignatureCheckException(String err) {
        super(err);
    }
}
