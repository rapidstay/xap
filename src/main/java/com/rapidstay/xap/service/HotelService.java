package com.rapidstay.xap.service;

import com.rapidstay.xap.client.ExpediaClient;
import com.rapidstay.xap.dto.HotelDetailResponse;
import com.rapidstay.xap.dto.HotelResponse;
import com.rapidstay.xap.dto.HotelSearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HotelService {

    private final ExpediaClient expediaClient;
    private final CityService cityService; // ✅ 추가

    @Value("${rapidstay.mock.enabled:true}")
    private boolean useMock;

    public HotelService(ExpediaClient expediaClient, CityService cityService) {
        this.expediaClient = expediaClient;
        this.cityService = cityService;
    }

    /** ✅ 도시명 → regionId 변환 후 검색 */
    public List<HotelResponse> searchHotels(String cityName, String checkIn, String checkOut, List<HotelSearchRequest.RoomInfo> rooms) {
        Optional<Long> regionOpt = cityService.findRegionId(cityName);
        if (regionOpt.isEmpty()) {
            System.out.println("⚠️ 지역 매칭 실패: " + cityName);
            return Collections.emptyList();
        }
        long regionId = regionOpt.get();

        if (useMock) {
            System.out.println("🧪 [Mock Mode] 지역 ID: " + regionId + " — Mock 데이터 반환");
            return mockHotels(cityName);
        } else {
            System.out.println("🌐 [Live Mode] Expedia API 호출 — regionId: " + regionId);
            return expediaClient.searchHotelsByRegion(regionId, checkIn, checkOut, rooms);
        }
    }

    private List<HotelResponse> mockHotels(String city) {
        List<HotelResponse> hotels = new ArrayList<>();
        hotels.add(HotelResponse.builder()
                .id(1L)
                .name("RapidStay Hotel")
                .address("서울특별시 중구 세종대로 110")
                .city(city)
                .rating(4.7)
                .latitude(37.5665)
                .longitude(126.9780)
                .lowestPrice("175000")
                .build());

        hotels.add(HotelResponse.builder()
                .id(2L)
                .name("Eden Garden Hotel")
                .address("부산광역시 해운대구 해운대로 321")
                .city("Busan")
                .rating(4.5)
                .latitude(35.1587)
                .longitude(129.1604)
                .lowestPrice("185000")
                .build());

        return hotels;
    }

    public List<HotelResponse> searchHotelsWithRooms(HotelSearchRequest request) {
        return searchHotels(request.getCity(), request.getCheckIn(), request.getCheckOut(), request.getRooms());
    }

    public HotelDetailResponse getHotelDetail(String hotelId,
                                              String city,
                                              String checkIn,
                                              String checkOut,
                                              List<HotelSearchRequest.RoomInfo> rooms) {

        List<HotelResponse> results = searchHotels(city, checkIn, checkOut, rooms);
        HotelResponse base = results.stream()
                .filter(h -> String.valueOf(h.getId()).equals(hotelId))
                .findFirst()
                .orElse(null);

        if (base == null) return null;

        return HotelDetailResponse.builder()
                .id(base.getId())
                .name(base.getName())
                .address(base.getAddress())
                .city(base.getCity())
                .rating(base.getRating())
                .latitude(base.getLatitude())
                .longitude(base.getLongitude())
                .description("이 호텔은 Mock 데이터 기반이며 Expedia 연동 시 실제 데이터로 교체됩니다.")
                .images(List.of(
                        "https://picsum.photos/seed/" + base.getName() + "/800/400",
                        "https://picsum.photos/seed/" + base.getName() + "2/800/400",
                        "https://picsum.photos/seed/" + base.getName() + "3/800/400"
                ))
                .amenities(List.of("무료 Wi-Fi", "레스토랑", "피트니스 센터", "수영장"))
                .rooms(buildMockRooms(base.getName()))
                .build();
    }

    private List<HotelDetailResponse.RoomDetail> buildMockRooms(String hotelName) {
        List<HotelDetailResponse.RoomDetail> list = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            list.add(HotelDetailResponse.RoomDetail.builder()
                    .roomName("디럭스룸 " + i)
                    .bedType("킹베드")
                    .amenities(List.of("무료 Wi-Fi", "TV", "냉장고"))
                    .description("편안한 객실로 가족 및 출장객 모두에게 적합합니다.")
                    .images(List.of("https://picsum.photos/seed/" + hotelName + "room" + i + "/400/250"))
                    .originalPrice(220000.0 + (i * 10000.0))
                    .finalPrice(190000.0 + (i * 10000.0))
                    .cancellationPolicy("체크인 2일 전까지 무료 취소")
                    .build());
        }
        return list;
    }
}
