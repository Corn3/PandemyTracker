package com.robin.PandemyTracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Region {
    private String name;
    private Week[] weekData;
    private int totalCases;
    private int totalDeaths;
    private int totalIntenseNursed;

    public Region(
            @JsonProperty("region_name") String name,
            @JsonProperty("total_cases") int totalCases,
            @JsonProperty("total_deaths") int totalDeaths,
            @JsonProperty("total_intense_nursed") int totalIntenseNursed,
            @JsonProperty("week_data") Week[] weekData
    ) {
        this.name = name;
        this.totalCases = totalCases;
        this.totalDeaths = totalDeaths;
        this.totalIntenseNursed = totalIntenseNursed;
        this.weekData = weekData;
    }

    public String getName() {
        return name;
    }

    public Week[] getWeekData() {
        return weekData;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public int getTotalIntenseNursed() {
        return totalIntenseNursed;
    }
}
