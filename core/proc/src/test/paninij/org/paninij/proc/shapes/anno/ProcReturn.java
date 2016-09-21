package org.paninij.proc.shapes.anno;

public class ProcReturn {
    private int x, y;
    
    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void print() {
        System.out.println(x + " " + y);
    }
}
