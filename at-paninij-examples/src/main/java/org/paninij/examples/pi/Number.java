
package org.paninij.examples.pi;

public class Number
{
    private double value;
    
    public Number() {
        this.value = 0;
    }

    public Number(double value) {
        this.value = value;
    }

    public void incr() {
        this.value++;
    }

    public double value() {
        return this.value;
    }
}
