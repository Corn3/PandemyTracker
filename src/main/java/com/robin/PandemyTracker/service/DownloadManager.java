package com.robin.PandemyTracker.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.ApplicationArguments;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
public class DownloadManager {

    private static final String DATA_URL_PATH = "https://www.arcgis.com/sharing/rest/content/items/b5e7488e117749c19881cce45db13f7e/data";
    private String path;

    public DownloadManager(ApplicationArguments args) throws Exception {
        List<String> urls = args.getNonOptionArgs();
        if (urls.size() < 1) {
            path = DATA_URL_PATH;
        } else if (urls.size() == 1) {
            path = urls.get(0);
        } else {
            throw new Exception("Too many arguments. Should be in URL format (i.e., www.test.com)");
        }

    }

    @Cacheable(value = "downloads")
    public Workbook getData() {
        Workbook workbook = null;
        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(path).openStream())) {
            workbook = new XSSFWorkbook(inputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }

}
