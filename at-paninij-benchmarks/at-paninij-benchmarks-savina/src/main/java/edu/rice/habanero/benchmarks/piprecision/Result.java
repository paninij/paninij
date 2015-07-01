package edu.rice.habanero.benchmarks.piprecision;

import java.math.BigDecimal;

public class Result
{
    private BigDecimal result;
    private int indx;

    public Result() { }

    public Result(BigDecimal result, int indx) {
        this.result = result;
        this.indx = indx;
    }

    public BigDecimal getResult() { return result; }
    public int getIndex() { return indx; }

}
