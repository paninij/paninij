package edu.rice.habanero.benchmarks.piprecision;

import java.math.BigDecimal;

public class Result
{
    private BigDecimal value;
    private int indx;

    public Result() { }

    public Result(BigDecimal value, int indx) {
        this.value = value;
        this.indx = indx;
    }

    public BigDecimal getValue() { return value; }
    public int getIndex() { return indx; }

}
