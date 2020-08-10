package com.robin.PandemyTracker.dao;

import com.robin.PandemyTracker.model.Region;

import java.util.List;
import java.util.Optional;

public interface RegionDao {

    int insertRegion(Region region);

    int[] batchInsertRegions(List<Region> regions);

    List<Region> selectAllRegions();

    Optional<Region> selectRegionByName(String name);

    int updateRegionByName(String name, Region region);

    int[] batchUpdateRegion(List<Region> regions);

}
