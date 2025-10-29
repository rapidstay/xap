package com.rapidstay.xap.controller;

import com.rapidstay.xap.dto.HotelResponse;
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

    @GetMapping("/")
    public String home() {
        return "✅ XAP Hotel API Server is running!";
    }

    @GetMapping("/hotel/test")
    public String testHotelApi() {
        return "🏨 Test Hotel endpoint OK";
    }

    @GetMapping("/search")
    public List<HotelResponse> searchHotels(
            @RequestParam String city,
            @RequestParam String checkIn,
            @RequestParam String checkOut
    ) {
        return hotelService.searchHotels(city, checkIn, checkOut);
    }

    @GetMapping("/detail")
    public HotelResponse getHotelDetail(@RequestParam String hotelId) {
        return hotelService.getHotelDetailById(hotelId);
    }

}
