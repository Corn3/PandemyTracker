package com.robin.PandemyTracker.dao;

import com.robin.PandemyTracker.model.Region;
import com.robin.PandemyTracker.model.Week;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository("postgresRegion")
public class RegionDataAccessService implements RegionDao {

    private JdbcTemplate jdbcTemplate;

    private static final String SELECT_SQL =
            "SELECT a.name, a.total_cases, a.total_deaths, a.total_intense_nursed, " +
                    "array_agg(array[b.week_number, b.cases, b.deaths, b.intense_nursed]) " +
                    "AS affected_region_weeks " +
                    "FROM pandemy_region a, pandemy_week b" +
                    "WHERE a.name = b.affected_region";
    private static final String UPDATE_SQL =
            "UPDATE pandemy_region" +
                    " SET total_cases = ?, total_deaths = ?, total_intense_nursed = ?" +
                    " WHERE name = ?";
    private static final String INSERT_SQL =
            "INSERT INTO pandemy_region (name, total_cases, total_deaths, total_intense_nursed) VALUES (?, ?, ?, ?)";


    @Autowired
    public RegionDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //NEED TO FIRST ADD WEEK DATA AND THEN CHECK IF REGION EXISTS, IF NOT ADD REGION, ELSE UPDATE TOTAL FOR GIVEN REGION
    @Override
    public int insertRegion(Region region) {
        //DONT FORGET ARGS FROM REGION AND WEEK DATA
        return jdbcTemplate.update(INSERT_SQL);
    }

    @Override
    public int[] batchInsertRegions(List<Region> regions) {
        return jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {

            }

            @Override
            public int getBatchSize() {
                return regions.size();
            }
        });
    }

    @Override
    public List<Region> selectAllRegions() {
        final String sql = SELECT_SQL + " GROUP BY a.name";
        return jdbcTemplate.query(sql, (resultSet, i) -> {

            String[] weeksArray = (String[]) resultSet.getArray("affected_region_weeks").getArray();
            List<Week> weeks = new ArrayList<>();
            for(int n = 0; n < weeksArray.length; n++) {
                String[] weeksString = weeksArray[n].split(",");
                if(weeksString.length == 4) {
                    weeks.add(convertStringDataToWeek(weeksString));
                } else {
                    throw new IllegalArgumentException("Shouldn't happen");
                }
            }

            return new Region(
                    resultSet.getString("name"),
                    resultSet.getInt("total_cases"),
                    resultSet.getInt("total_deaths"),
                    resultSet.getInt("total_intense_nursed"),
                    weeks
            );
        });
    }

    private Week convertStringDataToWeek(String[] weeksString) {
        return new Week(
                Integer.parseInt(weeksString[0]),
                Integer.parseInt(weeksString[1]),
                Integer.parseInt(weeksString[2]),
                Integer.parseInt(weeksString[3]));
    }

    @Override
    public Optional<Region> selectRegionByName(String name) {
        final String sql = SELECT_SQL + " AND a.name = '" + name + "' GROUP BY a.name";
        List<Region> regions = jdbcTemplate.query(sql, (resultSet, i) -> {

            String[] weeksArray = (String[]) resultSet.getArray("affected_region_weeks").getArray();
            List<Week> weeks = new ArrayList<>();
            for(int n = 0; n < weeksArray.length; n++) {
                String[] weeksString = weeksArray[n].split(",");
                if(weeksString.length == 4) {
                    weeks.add(convertStringDataToWeek(weeksString));
                } else {
                    throw new IllegalArgumentException("Shouldn't happen");
                }
            }

            return new Region(
                    resultSet.getString("name"),
                    resultSet.getInt("total_cases"),
                    resultSet.getInt("total_deaths"),
                    resultSet.getInt("total_intense_nursed"),
                    weeks
            );
        });
        return regions.stream().findFirst();
    }

    @Override
    public int updateRegionByName(String name, Region region) {
        return 0;
    }

    @Override
    public int[] batchUpdateRegion(List<Region> regions) {
        return jdbcTemplate.batchUpdate(UPDATE_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {

            }

            @Override
            public int getBatchSize() {
                return regions.size();
            }
        });
    }
}
