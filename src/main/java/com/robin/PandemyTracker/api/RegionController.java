package com.robin.PandemyTracker.api;

import com.robin.PandemyTracker.model.Region;
import com.robin.PandemyTracker.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/region")
@RestController
public class RegionController {

    private RegionService regionService;

    @Autowired
    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @PostMapping
    public int addRegion(@RequestBody Region region) {
        return regionService.addRegion(region);
    }

    @PostMapping(value = "/regions")
    public int[] addAllRegions(@RequestBody List<Region> regions) {
        // List<Region> regions = regionServiceClient.getAllRegions();
        return regionService.addAllRegions(regions);
    }

    @GetMapping
    public List<Region> getAllRegions() {
        return regionService.getAllRegions();
    }

    @GetMapping(path = "{name}")
    public Region getRegionByName(@PathVariable("name") String name) {
        return regionService.getRegionByName(name)
                .orElse(null);
    }

    @PutMapping(path = "{name}")
    public void updateRegion(@PathVariable("name") String name,
                            @NonNull @RequestBody Region region) {
        regionService.updateRegionByName(name, region);
    }

    @PutMapping
    public void updateAllRegions(@RequestBody List<Region> regions) {
        // List<Region> regions = regionServiceClient.getAllRegions();
        regionService.updateAllRegions(regions);
    }
}
