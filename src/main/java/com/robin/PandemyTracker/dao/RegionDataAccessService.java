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
import java.util.List;
import java.util.Optional;

@Repository("postgres")
public class RegionDataAccessService implements RegionDao {

    private JdbcTemplate jdbcTemplate;

    private static final String SELECT_SQL =
            "SELECT name, total_cases, total_deaths, total_intense_nursed" +
            " FROM pandemy_region";
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
        return jdbcTemplate.query(SELECT_SQL, (resultSet, i) -> {
            return new Region(
                    resultSet.getString("name"),
                    resultSet.getInt("total_cases"),
                    resultSet.getInt("total_deaths"),
                    resultSet.getInt("total_intense_nursed"),
                    new ArrayList<Week>()
                    // THIS NEEDS TO CALL METHOD IN A CLASS THAT WORKS WITH WEEK DATA AND FETCH ALL:
                    // Something in style of weekDataAccessService.selectWeekDataByRegion(resultSet.getString("name"));
                    // Alternatively write SQL that selects the weeks for a given region, something in style of:
                    // SELECT affected_region, week_number, cases, deaths, intense_nursed
                    // FROM pandemy_week
                    // WHERE pandemy_week.affected_region = pandemy_region.name
            );
        });
    }

    @Override
    public Optional<Region> selectRegionByName(String name) {
        return Optional.empty();
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
