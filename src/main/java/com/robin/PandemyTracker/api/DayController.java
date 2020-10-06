package com.robin.PandemyTracker.api;

import com.robin.PandemyTracker.model.Day;
import com.robin.PandemyTracker.service.DayDataManager;
import com.robin.PandemyTracker.service.DayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("api/v1/day")
@RestController
public class DayController {

    private final DayService dayService;
    private final DayDataManager dayDataManager;

    @Autowired
    public DayController(DayService dayService, DayDataManager dayDataManager) {
        this.dayDataManager = dayDataManager;
        this.dayService = dayService;
    }

    @CachePut(value = "dates", key="'DatesInCache'+#day.getDate()")
    @PostMapping
    public void addDay(@RequestBody Day day) {
        dayService.addDay(day);
    }

    @CachePut(value = "dates")
    @PostMapping(value = "/days")
    public void addAllDays() {
        List<Day> days = dayDataManager.getAllDays();
        dayService.addAllDays(days);
    }

    @Cacheable(value = "dates")
    @GetMapping
    public List<Day> getAllDays() {
        return dayService.getAllDays();
    }

    @Cacheable(value = "dates", key = "'DatesInCache'+#date")
    @GetMapping(path = "{date}")
    public Day getDayByDate(@PathVariable("date") LocalDate date) {
        Day day = dayService.getDayByDate(date)
                .orElse(null);
        return day;
    }

    @CachePut(value = "dates", key = "'DatesInCache'+#date")
    @PutMapping(path = "{date}")
    public void updateDay(@PathVariable("date") LocalDate date,
                         @NonNull @RequestBody Day day) {
        dayService.updateDay(date, day);
    }

    @CacheEvict(value = "dates", allEntries = true)
    @PutMapping
    public void updateAllDays() {
        List<Day> days = dayDataManager.getAllDays();
        dayService.updateAllDays(days);
    }

}
