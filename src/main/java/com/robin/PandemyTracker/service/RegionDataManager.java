package com.robin.PandemyTracker.service;

import com.robin.PandemyTracker.model.Region;
import com.robin.PandemyTracker.model.Week;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RegionDataManager {

    private DownloadManager downloadManager;
    private static final int REGION_TOTAL_DATA_SHEET = 3;
    private static final int REGION_WEEK_DATA_SHEET = 6;

    @Autowired
    public RegionDataManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    @Cacheable(value = "region_data", key = "'Region_Data'")
    public List<Region> getAllRegions() {
        return getRegionDataFromWorkbook(downloadManager.getData());
    }

    private List<Region> getRegionDataFromWorkbook(Workbook workbook) {
        List<Region> regions = new ArrayList<>();

        Sheet regionTotalSheet = workbook.getSheetAt(REGION_TOTAL_DATA_SHEET);

        for(int row = 1; row < regionTotalSheet.getLastRowNum(); row++) {
            String regionName = "";
            int cases = 0, deaths = 0, intenseNursed = 0, casesPerHundredThousand = 0;
            for(int col = 0; col < regionTotalSheet.getRow(row).getLastCellNum(); col++) {
                switch (regionTotalSheet.getRow(0).getCell(col).getStringCellValue().toLowerCase()) {
                    case "region":
                        regionName = regionTotalSheet.getRow(row).getCell(col).getStringCellValue();
                        break;
                    case "totalt_antal_fall":
                        cases = (int)regionTotalSheet.getRow(row).getCell(col).getNumericCellValue();
                        break;
                    case "fall_per_100000_inv":
                        casesPerHundredThousand = (int)regionTotalSheet.getRow(row).getCell(col).getNumericCellValue();
                        break;
                    case "totalt_antal_intensivvårdade":
                        intenseNursed = (int)regionTotalSheet.getRow(row).getCell(col).getNumericCellValue();
                        break;
                    case "totalt_antal_avlidna":
                        deaths = (int)regionTotalSheet.getRow(row).getCell(col).getNumericCellValue();
                        break;
                }
                if(col == regionTotalSheet.getRow(row).getLastCellNum() - 1) {
                    List<Week> weeks = getWeekDataFromRegionWorkbook(regionName, workbook);
                    Region region = new Region(regionName, cases, casesPerHundredThousand, deaths,
                            intenseNursed, weeks);
                    regions.add(region);
                }
            }
        }


        return regions;
    }

    private List<Week> getWeekDataFromRegionWorkbook(String regionName, Workbook workbook) {
        List<Week> weeks = new ArrayList<>();
        Sheet regionWeekSheet = workbook.getSheetAt(REGION_WEEK_DATA_SHEET);
        boolean regionFound = false;

        for(int row = 1; row < regionWeekSheet.getLastRowNum(); row++) {
            int weekNo = 0, cases = 0, cCases = 0, intenseCare = 0, cIntenseCared = 0,
            deaths = 0, cDeaths = 0, casesPerHundredthousand = 0, cumulativeCpht = 0;
            String regionN = "";
            for(int col = 0; col < regionWeekSheet.getRow(row).getLastCellNum(); col++) {
                switch(regionWeekSheet.getRow(0).getCell(col).getStringCellValue().toLowerCase()) {
                    case "veckonummer":
                        weekNo = (int)regionWeekSheet.getRow(row).getCell(col).getNumericCellValue();
                        break;
                    case "region":
                        regionN = regionWeekSheet.getRow(row).getCell(col).getStringCellValue();
                        if(regionN.equals(regionName)) {
                            regionFound = true;
                        } else if(regionFound == true && !regionN.equals(regionName)) {
                            return weeks;
                        }
                        break;
                    case "antal_fall_vecka":
                        cases = (int)regionWeekSheet.getRow(row).getCell(col).getNumericCellValue();
                        break;
                    case "kum_antal_fall":
                        cCases = (int)regionWeekSheet.getRow(row).getCell(col).getNumericCellValue();
                        break;
                    case "antal_intensivvårdade_vecka":
                        intenseCare = (int)regionWeekSheet.getRow(row).getCell(col).getNumericCellValue();
                        break;
                    case "kum_antal_intensivvårdade":
                        cIntenseCared = (int)regionWeekSheet.getRow(row).getCell(col).getNumericCellValue();
                        break;
                    case "antal_avlidna_vecka":
                        deaths = (int)regionWeekSheet.getRow(row).getCell(col).getNumericCellValue();
                        break;
                    case "kum_antal_avlidna":
                        cDeaths = (int)regionWeekSheet.getRow(row).getCell(col).getNumericCellValue();
                        break;
                    case "antal_fall_100000inv_vecka":
                        casesPerHundredthousand = (int)regionWeekSheet.getRow(row).getCell(col).getNumericCellValue();
                        break;
                    case "kum_fall_100000inv":
                        cumulativeCpht = (int)regionWeekSheet.getRow(row).getCell(col).getNumericCellValue();
                        break;
                }
            }
            if(regionFound == true) {
                Week week = new Week(weekNo, cases, cCases, deaths, cDeaths, intenseCare,
                        cIntenseCared, casesPerHundredthousand, cumulativeCpht);
                weeks.add(week);
            }

        }


        return weeks;
    }

}
