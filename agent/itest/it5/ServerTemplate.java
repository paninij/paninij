package it5;

import org.paninij.lang.Capsule;

@Capsule class ServerTemplate {

    Object state;

    public Object getObject() {
        state = new Object();
        return state;
    }
}
