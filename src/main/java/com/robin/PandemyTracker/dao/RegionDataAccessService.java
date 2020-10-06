package com.robin.PandemyTracker.dao;

import com.robin.PandemyTracker.model.Region;
import com.robin.PandemyTracker.model.Week;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository("postgresRegion")
public class RegionDataAccessService implements RegionDao {

    private JdbcTemplate jdbcTemplate;

    private static final String REGION_SELECT_SQL =
            "SELECT name, total_cases, cases_per_hundredthousand, total_deaths, total_intense_nursed " +
                    "FROM pandemy_region";

    private static final String WEEK_SELECT_SQL =
            "SELECT week_number, cases, cumulative_cases, deaths, cumulative_deaths, intense_nursed," +
                    " cumulative_intense_nursed, cases_per_hundredthousand, cumulative_cases_per_hundredthousand " +
                    "FROM pandemy_week " +
                    "WHERE affected_region = ";

    private static final String WEEK_INSERT_SQL =
            "INSERT INTO pandemy_week (affected_region, week_number, cases, cumulative_cases," +
                    " deaths, cumulative_deaths, intense_nursed, cumulative_intense_nursed," +
                    " cases_per_hundredthousand, cumulative_cases_per_hundredthousand) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String WEEK_UPDATE_SQL =
            "UPDATE pandemy_week" +
                    " SET cases = ?, cumulative_cases = ?, deaths = ?, cumulative_deaths = ?," +
                    " intense_nursed = ?, cumulative_intense_nursed = ?, cases_per_hundredthousand = ?," +
                    " cumulative_cases_per_hundredthousand = ?" +
                    " WHERE affected_region = ? AND week_number = ?";

    private static final String UPDATE_SQL =
            "UPDATE pandemy_region" +
                    " SET total_cases = ?, total_deaths = ?, total_intense_nursed = ?, cases_per_hundredthousand = ?" +
                    " WHERE name = ?";
    private static final String INSERT_SQL =
            "INSERT INTO pandemy_region (name, total_cases, total_deaths, total_intense_nursed, cases_per_hundredthousand)" +
                    " VALUES (?, ?, ?, ?, ?)";

    @Autowired
    public RegionDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertRegion(Region region) {
        batchInsertWeeks(region.getWeekData(), region.getName());
        return jdbcTemplate.update(INSERT_SQL, region.getName(),
                region.getTotalCases(), region.getTotalDeaths(),
                region.getTotalIntenseNursed(), region.getCasesPerHundredThousand());

    }

    private int[] batchInsertWeeks(List<Week> weeks, String regionName) {

        return jdbcTemplate.batchUpdate(WEEK_INSERT_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, regionName);
                ps.setInt(2, weeks.get(i).getWeekNumber());
                ps.setInt(3, weeks.get(i).getCases());
                ps.setInt(4, weeks.get(i).getCumulativeCases());
                ps.setInt(5, weeks.get(i).getDeaths());
                ps.setInt(6, weeks.get(i).getCumulativeDeaths());
                ps.setInt(7, weeks.get(i).getIntenseNursed());
                ps.setInt(8, weeks.get(i).getCumulativeIntenseNursed());
                ps.setInt(9, weeks.get(i).getCasesPerHundredThousand());
                ps.setInt(10, weeks.get(i).getCumulativeCasesPerHundredThousand());
            }

            @Override
            public int getBatchSize() {
                return weeks.size();
            }
        });
    }

    private int insertWeek(Week week, String affectedRegion) {
        return jdbcTemplate.update(WEEK_INSERT_SQL, affectedRegion,
                week.getWeekNumber(), week.getCases(), week.getCumulativeCases(), week.getDeaths(),
                week.getCumulativeDeaths(), week.getIntenseNursed(), week.getCumulativeIntenseNursed(),
                week.getCasesPerHundredThousand(), week.getCumulativeCasesPerHundredThousand());
    }

    private int[] batchUpdateWeeks(List<Week> weeks, String regionName) {
        return jdbcTemplate.batchUpdate(WEEK_UPDATE_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Optional<Week> oldWeek = selectAffectedRegionWeekByNumber(regionName, weeks.get(i).getWeekNumber());
                if(oldWeek.isEmpty()) {
                    insertWeek(weeks.get(i), regionName);
                } else {
                    ps.setInt(1, weeks.get(i).getCases());
                    ps.setInt(2, weeks.get(i).getCumulativeCases());
                    ps.setInt(3, weeks.get(i).getDeaths());
                    ps.setInt(4, weeks.get(i).getCumulativeDeaths());
                    ps.setInt(5, weeks.get(i).getIntenseNursed());
                    ps.setInt(6, weeks.get(i).getCumulativeIntenseNursed());
                    ps.setInt(7, weeks.get(i).getCasesPerHundredThousand());
                    ps.setInt(8, weeks.get(i).getCumulativeCasesPerHundredThousand());
                    ps.setString(9, regionName);
                    ps.setInt(10, weeks.get(i).getWeekNumber());
                }
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
                ps.setInt(5, regions.get(i).getCasesPerHundredThousand());
                batchInsertWeeks(regions.get(i).getWeekData(), regions.get(i).getName());
            }

            @Override
            public int getBatchSize() {
                return regions.size();
            }
        });
    }

    @Override
    public List<Region> selectAllRegions() {
        return jdbcTemplate.query(REGION_SELECT_SQL, (resultSet, i) -> {
            String affectedRegion = resultSet.getString("name");
            String sql = WEEK_SELECT_SQL + "'" + affectedRegion + "'";
            List<Week> weeks = jdbcTemplate.query(sql, (rs, j) -> {
                return new Week(
                        rs.getInt("week_number"),
                        rs.getInt("cases"),
                        rs.getInt("cumulative_cases"),
                        rs.getInt("deaths"),
                        rs.getInt("cumulative_deaths"),
                        rs.getInt("intense_nursed"),
                        rs.getInt("cumulative_intense_nursed"),
                        rs.getInt("cases_per_hundredthousand"),
                        rs.getInt("cumulative_cases_per_hundredthousand")
                );
            });

            return new Region(
                    affectedRegion,
                    resultSet.getInt("total_cases"),
                    resultSet.getInt("cases_per_hundredthousand"),
                    resultSet.getInt("total_deaths"),
                    resultSet.getInt("total_intense_nursed"),
                    weeks
            );
        });
    }

    private Optional<Week> selectAffectedRegionWeekByNumber(String affectedRegion, int weekNumber) {
        String sql = WEEK_SELECT_SQL + "'" + affectedRegion + "' AND week_number = '" + weekNumber + "'";
        return jdbcTemplate.query(sql, (rs, i) -> {
            return new Week(
                    rs.getInt("week_number"),
                    rs.getInt("cases"),
                    rs.getInt("cumulative_cases"),
                    rs.getInt("deaths"),
                    rs.getInt("cumulative_deaths"),
                    rs.getInt("intense_nursed"),
                    rs.getInt("cumulative_intense_nursed"),
                    rs.getInt("cases_per_hundredthousand"),
                    rs.getInt("cumulative_cases_per_hundredthousand")
            );
        }).stream().findFirst();
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
                        rs.getInt("cumulative_cases"),
                        rs.getInt("deaths"),
                        rs.getInt("cumulative_deaths"),
                        rs.getInt("intense_nursed"),
                        rs.getInt("cumulative_intense_nursed"),
                        rs.getInt("cases_per_hundredthousand"),
                        rs.getInt("cumulative_cases_per_hundredthousand")
                );
            });

            return new Region(
                    affectedRegion,
                    resultSet.getInt("total_cases"),
                    resultSet.getInt("cases_per_hundredthousand"),
                    resultSet.getInt("total_deaths"),
                    resultSet.getInt("total_intense_nursed"),
                    weeks
            );
        }).stream().findFirst();
    }


    @Override
    public int updateRegionByName(String name, Region regionUpdate) {
        return selectRegionByName(name)
                .map(region -> {
                    if(region == null) {
                        return 0;
                    } else {
                        jdbcTemplate.update(UPDATE_SQL, regionUpdate.getTotalCases(),
                                regionUpdate.getTotalDeaths(),
                                regionUpdate.getTotalIntenseNursed(),
                                regionUpdate.getCasesPerHundredThousand(),
                                region.getName());
                        batchUpdateWeeks(regionUpdate.getWeekData(), region.getName());
                        return 1;
                    }
                })
                .orElse(0);
    }

    @Override
    public int[] batchUpdateRegion(List<Region> regions) {
        return jdbcTemplate.batchUpdate(UPDATE_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Optional<Region> oldRegion = selectRegionByName(regions.get(i).getName());
                if(oldRegion.isEmpty()) {
                    insertRegion(regions.get(i));
                } else {
                    Region oldRegionInfo = oldRegion.get();
                    ps.setInt(1, regions.get(i).getTotalCases());
                    ps.setInt(2, regions.get(i).getTotalDeaths());
                    ps.setInt(3, regions.get(i).getTotalIntenseNursed());
                    ps.setInt(4, regions.get(i).getCasesPerHundredThousand());
                    ps.setString(5, oldRegionInfo.getName());
                    batchUpdateWeeks(regions.get(i).getWeekData(), oldRegionInfo.getName());
                }
            }

            @Override
            public int getBatchSize() {
                return regions.size();
            }
        });
    }
}