package com.hiru.salon.booking.mapper;

import com.hiru.salon.booking.dto.BookingDTO;
import com.hiru.salon.booking.modal.Booking;

public class BookingMapper {
    public static BookingDTO toDo(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setSalonId(booking.getSalonId());
        bookingDTO.setCustomerId(booking.getCustomerId());
        bookingDTO.setStatus(booking.getStatus());
        bookingDTO.setStartTime(booking.getStartTime());
        bookingDTO.setEndTime(booking.getEndTime());
        bookingDTO.setServiceIds(booking.getServiceIds());
        return bookingDTO;
    }
}
