package edu.rice.habanero.benchmarks.banking;

public class Transaction {
    double amount;
    int dest;
    int src;

    public Transaction(int src, int dest, double amount) {
        this.src = src;
        this.dest = dest;
        this.amount = amount;
    }

    public int getSrc() { return src; }
    public int getDest() { return dest; }
    public double getAmount() { return amount; }
}
