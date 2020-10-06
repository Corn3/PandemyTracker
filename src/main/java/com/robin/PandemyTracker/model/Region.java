package com.robin.PandemyTracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Region {
    private String name;
    private int totalCases;
    private int casesPerHundredThousand;
    private int totalDeaths;
    private int totalIntenseNursed;
    private List<Week> weekData;

    public Region(
            @JsonProperty("region_name") String name,
            @JsonProperty("total_cases") int totalCases,
            @JsonProperty("cases_per_hundredthousand") int casesPerHundredThousand,
            @JsonProperty("total_deaths") int totalDeaths,
            @JsonProperty("total_intense_nursed") int totalIntenseNursed,
            @JsonProperty("week_data") List<Week> weekData
    ) {
        this.name = name;
        this.totalCases = totalCases;
        this.casesPerHundredThousand = casesPerHundredThousand;
        this.totalDeaths = totalDeaths;
        this.totalIntenseNursed = totalIntenseNursed;
        this.weekData = weekData;
    }

    public String getName() {
        return name;
    }

    public List<Week> getWeekData() {
        return weekData;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public int getCasesPerHundredThousand() { return casesPerHundredThousand; }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public int getTotalIntenseNursed() {
        return totalIntenseNursed;
    }

    @Override
    public String toString() {
        return "Total Cases: " + totalCases + ", Total Deaths: " + totalDeaths + ", Total Intense nursed: " +
                totalIntenseNursed + ", Cases Per Hundredthousand citizen: " + casesPerHundredThousand;
    }
}