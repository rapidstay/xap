package com.rapidstay.xap.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CityService {

    private static class CityInfo {
        public String country;
        public String city_kr;
        public String city_en;
        public long regionId;
    }

    private final List<CityInfo> cityList;

    public CityService() {
        this.cityList = loadRegions();
        System.out.println("🌍 CityService 로드 완료 — 총 " + cityList.size() + "개 지역");
    }

    /** JSON 파일 로드 */
    private List<CityInfo> loadRegions() {
        try {
            ClassPathResource resource = new ClassPathResource("static/data/regions.json");
            String json = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /** ✅ 도시명으로 regionId 검색 (한글/영문 모두 가능, 대소문자 무시) */
    public Optional<Long> findRegionId(String query) {
        if (query == null || query.isBlank()) return Optional.empty();

        String q = query.trim().toLowerCase();

        // 1순위: 완전 일치
        return cityList.stream()
                .filter(c -> c.city_kr.equalsIgnoreCase(q) || c.city_en.toLowerCase().equals(q))
                .map(c -> c.regionId)
                .findFirst()
                // 2순위: 부분 일치 (오타 대응)
                .or(() -> cityList.stream()
                        .filter(c -> c.city_kr.contains(query) || c.city_en.toLowerCase().contains(q))
                        .map(c -> c.regionId)
                        .findFirst());
    }

    /** ✅ 자동완성용 리스트 반환 (최대 10개) */
    public List<Map<String, Object>> suggestCities(String keyword) {
        if (keyword == null || keyword.isBlank()) return Collections.emptyList();
        String lower = keyword.toLowerCase();

        return cityList.stream()
                .filter(c -> c.city_kr.contains(keyword) || c.city_en.toLowerCase().contains(lower))
                .limit(10)
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("country", c.country);
                    map.put("city_kr", c.city_kr);
                    map.put("city_en", c.city_en);
                    map.put("regionId", c.regionId);
                    return map;
                })
                .collect(Collectors.toList());
    }
}
