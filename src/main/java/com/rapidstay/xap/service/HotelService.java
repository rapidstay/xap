package com.rapidstay.xap.service;

import com.rapidstay.xap.client.ExpediaClient;
import com.rapidstay.xap.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HotelService {

    private final ExpediaClient expediaClient;
    private final CityService cityService;

    @Value("${rapidstay.mock.enabled:true}")
    private boolean useMock;

    public HotelService(ExpediaClient expediaClient, CityService cityService) {
        this.expediaClient = expediaClient;
        this.cityService = cityService;
    }

    /** ✅ 도시명 → regionId 변환 후 호텔 목록 조회 + 페이징 적용 */
    public PagedResult<HotelResponse> searchHotels(HotelSearchRequest req) {
        // Expedia API로 전체 결과 조회
        List<HotelResponse> allHotels = expediaClient.searchHotelsByRegion(
                req.getCity(),
                req.getCheckIn(),
                req.getCheckOut(),
                req.getRooms()
        );

        // ✅ 페이징 계산
        int totalCount = allHotels.size();
        int page = Math.max(1, req.getPage());
        int pageSize = Math.max(1, req.getPageSize());
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalCount);

        List<HotelResponse> pagedHotels = allHotels.subList(start, end);

        return new PagedResult<>(page, pageSize, totalCount, pagedHotels);
    }

    /** ✅ 호텔 목록만 필요할 때 */
    public List<HotelResponse> searchHotelsWithRooms(HotelSearchRequest request) {
        return searchHotels(request).getHotels();
    }

    /** ✅ 상세 페이지용 — 특정 호텔 ID 기반 조회 */
    public HotelDetailResponse getHotelDetail(String hotelId,
                                              String city,
                                              String checkIn,
                                              String checkOut,
                                              List<HotelSearchRequest.RoomInfo> rooms) {

        // 요청 객체 구성
        HotelSearchRequest req = new HotelSearchRequest();
        req.setCity(city);
        req.setCheckIn(checkIn);
        req.setCheckOut(checkOut);
        req.setRooms(rooms);
        req.setPage(1);
        req.setPageSize(100); // 기본 100개만 로드

        // 호텔 목록에서 해당 ID 찾기
        List<HotelResponse> results = searchHotelsWithRooms(req);
        HotelResponse base = results.stream()
                .filter(h -> String.valueOf(h.getId()).equals(hotelId))
                .findFirst()
                .orElse(null);

        if (base == null) return null;

        // ✅ 상세 데이터 Mock 구성
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

    /** ✅ 테스트용 Mock Room 데이터 생성 */
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
