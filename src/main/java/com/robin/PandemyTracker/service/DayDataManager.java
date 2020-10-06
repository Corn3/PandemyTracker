package com.robin.PandemyTracker.service;

import com.robin.PandemyTracker.model.Day;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class DayDataManager {

    private final DownloadManager downloadManager;
    private static final String DATE_FILE_PATH = "src/main/resources/dates/first_date_case";
    private static final int DAY_DATA_CASE_SHEET = 0;
    private static final int DAY_DATA_DEATHS_SHEET = 1;
    private static final int DAY_DATA_INTENSE_NURSED_SHEET = 2;

    @Autowired
    public DayDataManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    @Cacheable(value = "day_data", key="'Day_Data'")
    public List<Day> getAllDays() {
        List<Day> days = new ArrayList<>();
        try {
            days = getDayDataFromWorkbook(downloadManager.getData());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return days;
    }

    private List<Day> getDayDataFromWorkbook(Workbook workbook) throws IOException, InvalidFormatException {
        List<Day> days = new ArrayList<>();

        Sheet casesSheet = workbook.getSheetAt(DAY_DATA_CASE_SHEET);
        Sheet deathsSheet = workbook.getSheetAt(DAY_DATA_DEATHS_SHEET);
        Sheet intenseNursedSheet = workbook.getSheetAt(DAY_DATA_INTENSE_NURSED_SHEET);

        int casesSheetLastRowNum = casesSheet.getLastRowNum();
        // -1 due to last row in deaths sheet having a information missing row.
        int deathsSheetLastRowNum = deathsSheet.getLastRowNum() - 1;
        int intenseNursedSheetLastRowNum = intenseNursedSheet.getLastRowNum();
        int firstDeathsRound = 0, firstIntenseNursedRound = 0;

        //Could do with a while loop/ for loop that goes through all sheets 1 by 1, would however decrease the speed.
        int i = 1;
        try {
            for (LocalDate date = getDateFromFile(casesSheet); !date.isEqual(LocalDate.now()); date = date.plusDays(1)) {
                int cases = getDataFromCell(i, 1, casesSheet);
                int deaths = 0, intenseNursed = 0;

                if (casesSheetLastRowNum > deathsSheetLastRowNum) {
                    firstDeathsRound++;
                } else {
                    deaths = getDataFromCell(i - firstDeathsRound, 1, deathsSheet);
                }

                if (casesSheetLastRowNum > intenseNursedSheetLastRowNum) {
                    firstIntenseNursedRound++;
                } else {
                    intenseNursed = getDataFromCell(i - firstIntenseNursedRound, 1, intenseNursedSheet);
                }

                Day day = new Day(date, cases, deaths, intenseNursed, 0, 0, 0);
                days.add(day);
                casesSheetLastRowNum--;
                i++;
            }
        } catch (NullPointerException e) {
            return days;
        }
        return days;
    }

    private int getDataFromCell(int row, int col, Sheet sheet) {
        return (int) sheet.getRow(row).getCell(col).getNumericCellValue();
    }

    private LocalDate getDateFromCell(int row, int col, Sheet sheet) throws DateTimeParseException {
        return sheet.getRow(row)
                .getCell(col)
                .getDateCellValue()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private LocalDate getDateFromFile(Sheet sheet) throws IOException {
        LocalDate date = LocalDate.parse(Files.readString(Paths.get(DATE_FILE_PATH)));
        try {
            LocalDate d = getDateFromCell(1, 0, sheet);

            if (d.compareTo(date) != 0) {
                try (PrintWriter writer = new PrintWriter(DATE_FILE_PATH)) {
                    writer.write(d.toString());
                    date = d;
                }
            }
            return date;
        } catch (DateTimeParseException dtpe) {
            return date;
        }
    }


}
