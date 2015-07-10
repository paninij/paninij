package edu.rice.habanero.benchmarks.logmap;

public class Result {
    private double term;
    private int id;

    public Result() { }

    public Result(double term, int id) {
        this.term = term;
        this.id = id;
    }

    public double getTerm() { return term; }
    public int getId() { return id; }
}
