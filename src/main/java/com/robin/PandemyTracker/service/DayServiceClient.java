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
public class DayServiceClient {
    private static final String DATA_URL_PATH = "https://www.arcgis.com/sharing/rest/content/items/b5e7488e117749c19881cce45db13f7e/data";
    private static final String DATE_FILE_PATH = "src/main/resources/dates/first_date_case";
    private String path;

    public DayServiceClient(ApplicationArguments args) throws Exception {
        List<String> urls = args.getNonOptionArgs();
        if (urls.size() < 1) {
            path = DATA_URL_PATH;
        } else if (urls.size() == 1) {
            path = urls.get(0);
        } else {
            throw new Exception("Too many arguments. Should be in URL format (i.e., www.test.com)");
        }

    }

    @Cacheable(value = "day_downloads", key="'Downloads'")
    public List<Day> getAllDays() {
        List<Day> days = new ArrayList<>();
        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(path).openStream())) {
            days = readDataFromFile(inputStream);
            return days;
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
        return days;
    }

    private List<Day> readDataFromFile(BufferedInputStream inputStream) throws IOException, InvalidFormatException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        List<Day> days = new ArrayList<>();

        Sheet casesSheet = workbook.getSheetAt(0);
        Sheet deathsSheet = workbook.getSheetAt(1);
        Sheet intenseNursedSheet = workbook.getSheetAt(2);

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
