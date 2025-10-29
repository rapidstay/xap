package com.rapidstay.xap.service;

import com.rapidstay.xap.config.ExpediaConfig;
import com.rapidstay.xap.dto.HotelResponse;
import com.rapidstay.xap.dto.RoomResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class HotelService {

    private final ExpediaConfig expediaConfig;

    public HotelService(ExpediaConfig expediaConfig) {
        this.expediaConfig = expediaConfig;
    }

    // ✅ Redis 캐싱 (30분 TTL)
    @Cacheable(value = "hotels", key = "#city + '_' + #checkIn + '_' + #checkOut", cacheManager = "redisCacheManager")
    public List<HotelResponse> searchHotels(String city, String checkIn, String checkOut) {
        System.out.println("📡 [Mock] Fetching hotel list for " + city);

        // 임시 Mock 데이터
        return Arrays.asList(
                new HotelResponse(
                        1L,
                        "Lotte Hotel",
                        city,
                        37.565, 126.978,
                        "서울 중구 을지로 30",
                        4.7,
                        "고급스러운 서비스와 전망이 유명한 5성급 호텔입니다.",
                        Arrays.asList("https://picsum.photos/seed/lotte/800/400"),
                        Arrays.asList("무료 Wi-Fi", "수영장", "피트니스센터", "조식 포함"),
                        Arrays.asList(
                                new RoomResponse("디럭스 트윈룸", "트윈", "체크인 1일 전까지 무료 취소", 250000, 200000,
                                        Arrays.asList("https://picsum.photos/seed/room1a/600/400",
                                                "https://picsum.photos/seed/room1b/600/400"),
                                        Arrays.asList("조식 포함", "욕조", "미니바")),
                                new RoomResponse("프리미엄 스위트", "더블", "환불 불가", 400000, 320000,
                                        Arrays.asList("https://picsum.photos/seed/room1a/600/400",
                                                "https://picsum.photos/seed/room1b/600/400"),
                                        Arrays.asList("조식 포함", "거실 분리형", "전망 객실"))
                        )
                ),
                new HotelResponse(
                        2L,
                        "Shilla Stay Gwanghwamun",
                        city,
                        37.574, 126.978,
                        "서울 종로구 신문로 1길 11",
                        4.4,
                        "도심 접근성이 뛰어나며 비즈니스 고객에게 인기 있는 4성급 호텔입니다.",
                        Arrays.asList("https://picsum.photos/seed/shilla/800/400"),
                        Arrays.asList("무료 Wi-Fi", "레스토랑", "비즈니스 센터"),
                        Arrays.asList(
                                new RoomResponse("스탠다드 더블", "더블", "체크인 2일 전까지 무료 취소", 180000, 150000,
                                        Arrays.asList("https://picsum.photos/seed/room1a/600/400",
                                                "https://picsum.photos/seed/room1b/600/400"),
                                        Arrays.asList("욕조", "조식 포함")),
                                new RoomResponse("이그제큐티브 스위트", "더블", "환불 불가", 300000, 240000,
                                        Arrays.asList("https://picsum.photos/seed/room1a/600/400",
                                                "https://picsum.photos/seed/room1b/600/400"),
                                        Arrays.asList("조식 포함", "전망 객실", "거실 분리형"))
                        )
                ),
                new HotelResponse(
                        3L,
                        "Signiel Seoul",
                        city,
                        37.513, 127.102,
                        "서울 송파구 올림픽로 300 롯데월드타워",
                        4.9,
                        "서울의 랜드마크 롯데월드타워에 위치한 최고급 럭셔리 호텔입니다.",
                        Arrays.asList("https://picsum.photos/seed/room1a/600/400",
                                "https://picsum.photos/seed/room1b/600/400"),
                        Arrays.asList("무료 Wi-Fi", "실내 수영장", "스파", "조식 포함"),
                        Arrays.asList(
                                new RoomResponse("시그니엘 디럭스룸", "킹", "체크인 3일 전까지 무료 취소", 450000, 390000,
                                        Arrays.asList("https://picsum.photos/seed/room1a/600/400",
                                                "https://picsum.photos/seed/room1b/600/400"),
                                        Arrays.asList("조식 포함", "전망 객실")),
                                new RoomResponse("로얄 스위트", "킹", "환불 불가", 900000, 720000,
                                        Arrays.asList("https://picsum.photos/seed/room1a/600/400",
                                                "https://picsum.photos/seed/room1b/600/400"),
                                        Arrays.asList("전망 객실", "거실 분리형", "전용 라운지 이용"))
                        )
                )
        );
    }

    // ✅ 상세 조회 (hotelId 기반)
    public HotelResponse getHotelDetailById(String hotelId) {
        return searchHotels("Seoul", "2025-11-01", "2025-11-03")
                .stream()
                .filter(h -> String.valueOf(h.getId()).equals(hotelId))
                .findFirst()
                .orElse(null);
    }
}
