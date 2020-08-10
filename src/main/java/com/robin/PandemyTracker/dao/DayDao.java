package com.robin.PandemyTracker.dao;

import com.robin.PandemyTracker.model.Day;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DayDao {
    int insertDay(Day day);

    List<Day> selectAllDays();

    Optional<Day> selectDayByDate(LocalDate date);

    int updateDayByDate(LocalDate date, Day dayUpdate);

    int[] batchInsertDay(List<Day> days);

    int[] batchUpdateDay(List<Day> days);
}
