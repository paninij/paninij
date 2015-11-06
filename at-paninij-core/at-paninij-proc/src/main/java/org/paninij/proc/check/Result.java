package org.paninij.proc.check;

import javax.lang.model.element.Element;

public interface Result
{
    boolean ok();
    String err();
    String source();
    Element offender();
    
    public static class Ok implements Result
    {
        public boolean ok() {
            return true;
        }
        public String err() {
            return null;
        }
        public String source() {
            return null;
        }
        public Element offender() {
            return null;
        }
        
    }
    
    public static Result ok = new Ok();
    
    public static class Error implements Result
    {
        private final String err;
        private final String source;
        private final Element offender;
        
        public Error(String err, String source) {
            this.err = err;
            this.source = source;
            this.offender = null;
        }
        
        public Error(String err, String source, Element offender) {
            this.err = err;
            this.source = source;
            this.offender = offender;
        }
        
        @Override
        public boolean ok() {
            return false;
        }
        
        @Override
        public String err() {
            return err;
        }

        @Override
        public String source() {
            return source;
        }
        
        @Override
        public Element offender() {
            return offender;
        }
    }
}
