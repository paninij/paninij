package org.paninij.model;

import java.util.ArrayList;

public class Capsule
{
    private String name;
    private ArrayList<Procedure> procedures;

    public Capsule(String name) {
        this.name = name;
        this.procedures = new ArrayList<Procedure>();
    }

    public ArrayList<Procedure> getProcedures() {
        return this.procedures;
    }

    public void addProcedure(Procedure p) {
        this.procedures.add(p);
    }
}
