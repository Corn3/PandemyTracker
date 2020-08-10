package com.robin.PandemyTracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class Day extends TimePeriod {
    private final LocalDate date;
    private int newCases;
    private int newDeaths;
    private int newIntenseNursed;

    public Day(@JsonProperty("test_date") LocalDate date,
               @JsonProperty("cases") int cases,
               @JsonProperty("deaths") int deaths,
               @JsonProperty("intense_nursed") int intenseNursed,
               @JsonProperty("new_cases") int newCases,
               @JsonProperty("new_deaths") int newDeaths,
               @JsonProperty("new_intense_nursed") int newIntenseNursed) {
        super(cases, deaths, intenseNursed);
        this.date = date;
        this.newCases = newCases;
        this.newDeaths = newDeaths;
        this.newIntenseNursed = newIntenseNursed;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getNewCases()  {
        return newCases;
    }

    public int getNewDeaths() {
        return newDeaths;
    }

    public int getNewIntenseNursed() {
        return newIntenseNursed;
    }

    @Override
    public String toString() {
        return "Date: " + date + ", " + super.toString() + ", new cases: " +
                newCases + ", new deaths: " + newDeaths + ", new intense nursed: " + newIntenseNursed;
    }
}
