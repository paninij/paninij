package org.paninij.proc.check;

public interface Result
{
    boolean ok();
    String err();
    String source();
    
    public static class OK implements Result
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
    }
    
    public static class Error implements Result
    {
        private final String err;
        private final String source;
        
        public Error(String err, String source) {
            this.err = err;
            this.source = source;
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
    }
}
