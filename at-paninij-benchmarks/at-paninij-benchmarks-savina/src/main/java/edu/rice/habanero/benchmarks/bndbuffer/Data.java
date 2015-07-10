package edu.rice.habanero.benchmarks.bndbuffer;

public class Data
{
    private double value;
    private int id;
    private int indx;

    public Data() {
    }

    public Data(double value, int id, int indx) {
        this.value = value;
        this.id = id;
        this.indx = indx;
    }

    public double getValue() {return value; }
    public int getId() { return id; }
    public int getResIndex() { return indx; }
}
