package com.rapidstay.xap.controller;

import com.rapidstay.xap.service.CityService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cities")
@CrossOrigin(origins = "*")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    /** ✅ 자동완성 및 검색 */
    @GetMapping("/search")
    public List<Map<String, Object>> searchCities(@RequestParam String query) {
        return cityService.suggestCities(query);
    }

    /** ✅ regionId 직접 조회 (도시명 → regionId) */
    @GetMapping("/region")
    public Map<String, Object> getRegionId(@RequestParam String name) {
        Optional<Long> regionId = cityService.findRegionId(name);
        return regionId.<Map<String, Object>>map(id -> Map.of(
                        "success", true,
                        "regionId", id))
                .orElse(Map.of("success", false, "message", "도시를 찾을 수 없습니다."));
    }
}
