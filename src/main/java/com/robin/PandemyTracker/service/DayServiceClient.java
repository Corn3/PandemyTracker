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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class DayServiceClient {
    private static final String DATA_URL_PATH = "https://www.arcgis.com/sharing/rest/content/items/b5e7488e117749c19881cce45db13f7e/data";
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
        for (int i = 1; i <= casesSheet.getLastRowNum(); i++) {
            LocalDate date = getDateFromCell(i, 0, casesSheet);
            int cases = getDataFromCell(i, 1, casesSheet);
            int deaths = 0, intenseNursed = 0;

            if(casesSheetLastRowNum > deathsSheetLastRowNum) {
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
        }
        return days;
    }

    private int getDataFromCell(int row, int col, Sheet sheet) {
        return (int) sheet.getRow(row).getCell(col).getNumericCellValue();
    }

    private LocalDate getDateFromCell(int row, int col, Sheet sheet) {
        return sheet.getRow(row)
                .getCell(col)
                .getDateCellValue()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }


}
