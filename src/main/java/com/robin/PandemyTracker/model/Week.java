package com.robin.PandemyTracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Week extends TimePeriod {

    private final int weekNumber;

    public Week(@JsonProperty("week_number") int weekNumber,
                @JsonProperty("cases") int cases,
                @JsonProperty("deaths") int deaths,
                @JsonProperty("intense_nursed") int intenseNursed) {
        super(cases, deaths, intenseNursed);
        this.weekNumber = weekNumber;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    @Override
    public String toString() {
        return "Week number: " + weekNumber + ", " + super.toString();
    }

}
