package com.robin.PandemyTracker.service;

import com.robin.PandemyTracker.dao.RegionDao;
import com.robin.PandemyTracker.model.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegionService {

    private final RegionDao regionDao;

    @Autowired
    public RegionService(@Qualifier("postgresRegion") RegionDao regionDao) {
        this.regionDao = regionDao;
    }

    public int addRegion(Region region) {
        return regionDao.insertRegion(region);
    }

    public int[] addAllRegions(List<Region> regions) {
        return regionDao.batchInsertRegions(regions);
    }

    public Optional<Region> getRegionByName(String name) {
        return regionDao.selectRegionByName(name);
    }

    public List<Region> getAllRegions() {
        return regionDao.selectAllRegions();
    }

    public int updateRegionByName(String name, Region region) {
        return regionDao.updateRegionByName(name, region);
    }

    public int[] updateAllRegions(List<Region> regions) {
        return regionDao.batchUpdateRegion(regions);
    }


}
