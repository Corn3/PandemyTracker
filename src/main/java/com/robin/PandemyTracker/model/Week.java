package com.robin.PandemyTracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Week extends TimePeriod {

    private final int weekNumber;
    private int casesPerHundredThousand;
    private int cumulativeCases;
    private int cumulativeDeaths;
    private int cumulativeIntenseNursed;
    private int cumulativeCasesPerHundredThousand;

    public Week(@JsonProperty("week_number") int weekNumber,
                @JsonProperty("cases") int cases,
                @JsonProperty("cumulative_cases") int cumulativeCases,
                @JsonProperty("deaths") int deaths,
                @JsonProperty("cumulative_deaths") int cumulativeDeaths,
                @JsonProperty("intense_nursed") int intenseNursed,
                @JsonProperty("cumulative_intense_nursed") int cumulativeIntenseNursed,
                @JsonProperty("cases_per_hundredthousand") int casesPerHundredThousand,
                @JsonProperty("cumulative_cases_per_hundredthousand") int cumulativeCasesPerHundredThousand) {
        super(cases, deaths, intenseNursed);
        this.weekNumber = weekNumber;
        this.cumulativeCases = cumulativeCases;
        this.cumulativeDeaths = cumulativeDeaths;
        this.cumulativeIntenseNursed = cumulativeIntenseNursed;
        this.casesPerHundredThousand = casesPerHundredThousand;
        this.cumulativeCasesPerHundredThousand = cumulativeCasesPerHundredThousand;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public int getCasesPerHundredThousand() { return casesPerHundredThousand; }

    public int getCumulativeCases() { return cumulativeCases; }

    public int getCumulativeDeaths() { return cumulativeDeaths; }

    public int getCumulativeIntenseNursed() { return cumulativeIntenseNursed; }

    public int getCumulativeCasesPerHundredThousand() { return cumulativeCasesPerHundredThousand; }

    @Override
    public String toString() {
        return "Week number: " + weekNumber + ", " + super.toString() + ", CumulativeCases: " + cumulativeCases +
                ", CumulativeDeaths: " + cumulativeDeaths + ", CumulativeIntense: " + cumulativeIntenseNursed +
                ", CasesPerHundredThousand: " + casesPerHundredThousand + ", CumulativeCasesPerHundredThousand: " +
                cumulativeCasesPerHundredThousand;
    }

}
