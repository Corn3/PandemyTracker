package com.robin.PandemyTracker.model;

import java.io.Serializable;

public abstract class TimePeriod implements Serializable {
    private int cases;
    private int deaths;
    private int intenseNursed;

    public TimePeriod(int cases, int deaths, int intenseNursed) {
        this.cases = cases;
        this.deaths = deaths;
        this.intenseNursed = intenseNursed;
    }

    public int getCases() {
        return cases;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getIntenseNursed() {
        return intenseNursed;
    }

    @Override
    public String toString() {
        return "Cases: " + getCases() + ", Deaths: " + getDeaths() + ", intense nursed: " + getIntenseNursed();
    }
}
