package com.hiru.salon.booking.controller;

import com.hiru.salon.booking.dto.BookingRequest;
import com.hiru.salon.booking.dto.SalonDTO;
import com.hiru.salon.booking.dto.ServiceDTO;
import com.hiru.salon.booking.dto.UserDTO;
import com.hiru.salon.booking.modal.Booking;
import com.hiru.salon.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestParam Long salonId,
            @RequestBody BookingRequest bookingRequest
            ) throws Exception {
        UserDTO user = new UserDTO();
        user.setId(1L);

        SalonDTO salon = new SalonDTO();
        salon.setId(salonId);

        Set<ServiceDTO> serviceDTOSet = new HashSet<>();

        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setId(1L);
        serviceDTO.setName("Hair cut for men");
        serviceDTO.setPrice(350.0);
        serviceDTO.setDuration(45);

        serviceDTOSet.add(serviceDTO);

        Booking booking = bookingService.createBooking(bookingRequest, user, salon, serviceDTOSet);
        return ResponseEntity.ok(booking);
    }
}
