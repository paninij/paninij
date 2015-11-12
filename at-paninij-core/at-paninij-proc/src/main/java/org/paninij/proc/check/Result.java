package org.paninij.proc.check;

import javax.lang.model.element.Element;

public interface Result
{
    boolean ok();
    String err();
    Class<? extends Check> source();
    
    /**
     * May return null.
     */
    Element offender();
    
    public static Result ok = new Result()
    {
        public boolean ok() {
            return true;
        }
        public String err() {
            return null;
        }
        public Class<? extends Check> source() {
            return null;
        }
        public Element offender() {
            return null;
        }
    };
    
    /**
     * @throw  IllegalArgumentException  If `err` or `source` is null.
     */
    public static class Error implements Result
    {
        private final String err;
        private final Class<? extends Check> source;
        private final Element offender;
        
        public Error(String err, Class<? extends Check> source, Element offender) {
            if (err == null || source == null) {
                throw new IllegalArgumentException();
            }
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
        public Class<? extends Check> source() {
            return source;
        }
        
        @Override
        public Element offender() {
            return offender;
        }
    }
}
