package org.paninij.examples.pi;

public class Number
{
    double value;

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

    public static double total(Number[] numbers) {
        double total = 0;
        for (Number n : numbers) total += n.value();
        return total;
    }

}
