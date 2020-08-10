package com.robin.PandemyTracker.dao;

import com.robin.PandemyTracker.model.Day;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository("fakeDao")
public class FakeDayDataAccessService implements DayDao {

    private static List<Day> DB = new ArrayList<>();

    @Override
    public int insertDay(Day day) {
        DB.add(new Day(day.getDate(), day.getCases(), day.getDeaths(), day.getIntenseNursed(), 0, 0, 0));
        //DB.add(new Day(day.getTests(), day.getDeaths(), day.getIntenseNursed()));
        return 1;
    }

    @Override
    public List<Day> selectAllDays() {
        return DB;
    }

    @Override
    public Optional<Day> selectDayByDate(LocalDate date) {
        return DB.stream()
                .filter(day -> day.getDate().equals(date))
                .findFirst();
    }

    @Override
    public int updateDayByDate(LocalDate date, Day dayUpdate) {
        return selectDayByDate(date)
                .map(day -> {
                   int indexOfDayToUpdate = DB.indexOf(day);
                   if(indexOfDayToUpdate >= 0) {
                       DB.set(indexOfDayToUpdate, new Day(date, dayUpdate.getCases(), dayUpdate.getDeaths(), dayUpdate.getIntenseNursed(),
                               dayUpdate.getCases() - day.getCases(), dayUpdate.getDeaths() - day.getDeaths(), dayUpdate.getIntenseNursed() - day.getIntenseNursed()));
                       return 1;
                   }
                   return 0;
                })
                .orElse(0);
    }

    @Override
    public int[] batchInsertDay(List<Day> days) {
        return new int[0];
    }

    @Override
    public int[] batchUpdateDay(List<Day> days) {
        return new int[0];
    }
}
