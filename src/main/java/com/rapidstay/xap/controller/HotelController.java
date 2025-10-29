package com.rapidstay.xap.controller;

import com.rapidstay.xap.dto.HotelDetailRequest;
import com.rapidstay.xap.dto.HotelDetailResponse;
import com.rapidstay.xap.dto.HotelResponse;
import com.rapidstay.xap.dto.HotelSearchRequest;
import com.rapidstay.xap.service.HotelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // ✅ 서버 상태 체크용
    @GetMapping("/")
    public String home() {
        return "✅ XAP Hotel API Server is running!";
    }

    @GetMapping("/hotel/test")
    public String testHotelApi() {
        return "🏨 Test Hotel endpoint OK";
    }

    // ✅ 호텔 검색 (rooms 포함)
    @PostMapping("/search")
    public List<HotelResponse> searchHotelsWithRooms(@RequestBody HotelSearchRequest request) {
        System.out.println("🔎 [POST /search] 요청 수신: " + request);
        return hotelService.searchHotelsWithRooms(request);
    }

    // ✅ 상세 페이지 조회
    @PostMapping("/detail")
    public HotelDetailResponse getHotelDetail(@RequestBody HotelDetailRequest request) {
        System.out.println("📄 [POST /detail] 요청 수신: " + request);
        return hotelService.getHotelDetail(
                request.getHotelId(),
                request.getCity(),
                request.getCheckIn(),
                request.getCheckOut(),
                request.getRooms()
        );
    }
}
