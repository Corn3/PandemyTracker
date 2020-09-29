package com.robin.PandemyTracker.dao;

import com.robin.PandemyTracker.model.Day;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository("postgres")
public class DayDataAccessService implements DayDao {

    private final JdbcTemplate jdbcTemplate;

    private final String INSERT_SQL =
            "INSERT INTO pandemy_date (test_date, cases, deaths, intense_nursed, new_cases, new_deaths, new_intense_nursed)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?)";
    private final String UPDATE_SQL =
            "UPDATE pandemy_date " +
            "SET cases = ?, deaths = ?, intense_nursed = ?, new_cases = ?, new_deaths = ?, new_intense_nursed = ?" +
            " WHERE test_date = ?";
    private final String SELECT_SQL =
            "SELECT test_date, cases, deaths, intense_nursed, new_cases, new_deaths, new_intense_nursed" +
            " FROM pandemy_date";

    @Autowired
    public DayDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertDay(Day day) {
        return jdbcTemplate.update(INSERT_SQL, day.getDate(), day.getCases(), day.getDeaths(), day.getIntenseNursed(),
                0, 0, 0);
    }

    // Only runs to populate the database first time.
    @Override
    public int[] batchInsertDay(List<Day> days) {
        return jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setObject(1, days.get(i).getDate());
                ps.setInt(2, days.get(i).getCases());
                ps.setInt(3, days.get(i).getDeaths());
                ps.setInt(4, days.get(i).getIntenseNursed());
                ps.setInt(5, 0);
                ps.setInt(6, 0);
                ps.setInt(7, 0);
            }

            @Override
            public int getBatchSize() {
                return days.size();
            }
        });
    }

    @Override
    public List<Day> selectAllDays() {
        return jdbcTemplate.query(SELECT_SQL, (resultSet, i) -> {
            return new Day(
                    resultSet.getDate("test_date").toLocalDate(),
                    resultSet.getInt("cases"),
                    resultSet.getInt("deaths"),
                    resultSet.getInt("intense_nursed"),
                    resultSet.getInt("new_cases"),
                    resultSet.getInt("new_deaths"),
                    resultSet.getInt("new_intense_nursed")
            );
        });
    }

    @Override
    public Optional<Day> selectDayByDate(LocalDate date) {
        final String sql = SELECT_SQL +
                        " WHERE test_date = '" + date + "'";
        List<Day> days = jdbcTemplate.query(sql, (resultSet, i) -> {
            return new Day(
                    resultSet.getDate("test_date").toLocalDate(),
                    resultSet.getInt("cases"),
                    resultSet.getInt("deaths"),
                    resultSet.getInt("intense_nursed"),
                    resultSet.getInt("new_cases"),
                    resultSet.getInt("new_deaths"),
                    resultSet.getInt("new_intense_nursed")
            );
        });
        return days.stream().findFirst();
    }

    @Override
    public int updateDayByDate(LocalDate date, Day dayUpdate) {
        return selectDayByDate(date)
                .map(day -> {
                    if (day == null) {
                        return 0;
                    } else {
                        jdbcTemplate.update(UPDATE_SQL, dayUpdate.getCases(), dayUpdate.getDeaths(), dayUpdate.getIntenseNursed(),
                                dayUpdate.getCases() - day.getCases(), dayUpdate.getDeaths() - day.getDeaths(),
                                dayUpdate.getIntenseNursed() - day.getIntenseNursed(), date);
                        return 1;
                    }
                })
                .orElse(0);
    }

    @Override
    public int[] batchUpdateDay(List<Day> days) {
        return jdbcTemplate.batchUpdate(UPDATE_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Optional<Day> oldDay = selectDayByDate(days.get(i).getDate());
                if(oldDay.isEmpty()) {
                    insertDay(new Day(
                            days.get(i).getDate(), days.get(i).getCases(),
                            days.get(i).getDeaths(), days.get(i).getIntenseNursed(),
                            days.get(i).getCases(), days.get(i).getDeaths(),
                            days.get(i).getIntenseNursed()
                    ));
                }
                else {
                    Day oldDayInfo = oldDay.get();
                    ps.setInt(1, days.get(i).getCases());
                    ps.setInt(2, days.get(i).getDeaths());
                    ps.setInt(3, days.get(i).getIntenseNursed());
                    ps.setInt(4, days.get(i).getCases() - oldDayInfo.getCases());
                    ps.setInt(5, days.get(i).getDeaths() - oldDayInfo.getDeaths());
                    ps.setInt(6, days.get(i).getIntenseNursed() - oldDayInfo.getIntenseNursed());
                    ps.setObject(7, days.get(i).getDate());
                }
            }

            @Override
            public int getBatchSize() {
                return days.size();
            }
        });
    }
}
