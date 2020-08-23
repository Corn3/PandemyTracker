package com.robin.PandemyTracker.dao;

import com.robin.PandemyTracker.model.Region;
import com.robin.PandemyTracker.model.Week;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository("postgres")
public class RegionDataAccessService implements RegionDao {

    private JdbcTemplate jdbcTemplate;

    private static final String REGION_SELECT_SQL =
            "SELECT name, total_cases, total_deaths, total_intense_nursed " +
                    "FROM pandemy_region";

    private static final String WEEK_SELECT_SQL =
            "SELECT week_number, cases, deaths, intense_nursed " +
                    "FROM pandemy_week " +
                    "WHERE affected_region = ";

    private static final String WEEK_INSERT_SQL =
            "INSERT INTO pandemy_week (affected_region, week_number, cases, deaths, intense_nursed) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_SQL =
            "SELECT array_agg(week_number || ',' || cases || ',' || deaths || ',' || intense_nursed) " +
                    "AS affected_region_weeks " +
                    "FROM pandemy_week " +
                    "WHERE affected_region = ?";
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

    @Override
    public int insertRegion(Region region) {
        batchInsertWeeks(region.getWeekData(), region.getName());
        return jdbcTemplate.update(INSERT_SQL, region.getName(),
                region.getTotalCases(), region.getTotalDeaths(),
                region.getTotalIntenseNursed());

    }

    private int[] batchInsertWeeks(List<Week> weeks, String regionName) {

        return jdbcTemplate.batchUpdate(WEEK_INSERT_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, regionName);
                ps.setInt(2, weeks.get(i).getWeekNumber());
                ps.setInt(3, weeks.get(i).getCases());
                ps.setInt(4, weeks.get(i).getDeaths());
                ps.setInt(5, weeks.get(i).getIntenseNursed());
            }

            @Override
            public int getBatchSize() {
                return weeks.size();
            }
        });
    }

    @Override
    public int[] batchInsertRegions(List<Region> regions) {
        return jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, regions.get(i).getName());
                ps.setInt(2, regions.get(i).getTotalCases());
                ps.setInt(3, regions.get(i).getTotalDeaths());
                ps.setInt(4, regions.get(i).getTotalIntenseNursed());
                batchInsertWeeks(regions.get(i).getWeekData(), regions.get(i).getName());
            }

            @Override
            public int getBatchSize() {
                return regions.size();
            }
        });
    }

    @Override
    public List<Region> selectAllRegions() {;
        return jdbcTemplate.query(REGION_SELECT_SQL, (resultSet, i) -> {
            String affectedRegion = resultSet.getString("name");
            String sql = WEEK_SELECT_SQL + "'" + affectedRegion + "'";
            List<Week> weeks = jdbcTemplate.query(sql, (rs, j) -> {
                return new Week(
                        rs.getInt("week_number"),
                        rs.getInt("cases"),
                        rs.getInt("deaths"),
                        rs.getInt("intense_nursed")
                );
            });

            return new Region(
                    affectedRegion,
                    resultSet.getInt("total_cases"),
                    resultSet.getInt("total_deaths"),
                    resultSet.getInt("total_intense_nursed"),
                    weeks
            );
        });
    }

    @Override
    public Optional<Region> selectRegionByName(String name) {
        final String region_sql = REGION_SELECT_SQL + " WHERE name = '" + name + "'";
        return jdbcTemplate.query(region_sql, (resultSet, i) -> {

            String affectedRegion = resultSet.getString("name");
            String sql = WEEK_SELECT_SQL + "'" + affectedRegion + "'";
            List<Week> weeks = jdbcTemplate.query(sql, (rs, j) -> {
                return new Week(
                        rs.getInt("week_number"),
                        rs.getInt("cases"),
                        rs.getInt("deaths"),
                        rs.getInt("intense_nursed")
                );
            });

            return new Region(
                    affectedRegion,
                    resultSet.getInt("total_cases"),
                    resultSet.getInt("total_deaths"),
                    resultSet.getInt("total_intense_nursed"),
                    weeks
            );
        }).stream().findFirst();
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