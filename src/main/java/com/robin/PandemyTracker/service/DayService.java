package com.robin.PandemyTracker.service;

import com.robin.PandemyTracker.dao.DayDao;
import com.robin.PandemyTracker.model.Day;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DayService {

    private final DayDao dayDao;

    @Autowired
    public DayService(@Qualifier("postgres") DayDao dayDao) {
        this.dayDao = dayDao;
    }

    public int addDay(Day day) {
        return dayDao.insertDay(day);
    }

    public int[] addAllDays(List<Day> days) { return dayDao.batchInsertDay(days); }

    public int[] updateAllDays(List<Day> days) {
        return dayDao.batchUpdateDay(days);
    }

    public List<Day> getAllDays() {
        return dayDao.selectAllDays();
    }

    public Optional<Day> getDayByDate(LocalDate date) {
        return dayDao.selectDayByDate(date);
    }

    public int updateDay(LocalDate date, Day day) {
        return dayDao.updateDayByDate(date, day);
    }

}
