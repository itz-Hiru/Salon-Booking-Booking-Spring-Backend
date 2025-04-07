package com.hiru.salon.booking.service;

import com.hiru.salon.booking.domain.BookingStatus;
import com.hiru.salon.booking.dto.BookingRequest;
import com.hiru.salon.booking.dto.SalonDTO;
import com.hiru.salon.booking.dto.ServiceDTO;
import com.hiru.salon.booking.dto.UserDTO;
import com.hiru.salon.booking.modal.Booking;
import com.hiru.salon.booking.modal.SalonReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface BookingService {
    Booking createBooking(BookingRequest booking,
                          UserDTO userDTO,
                          SalonDTO salonDTO,
                          Set<ServiceDTO> serviceDTOSet) throws Exception;

    List<Booking> getBookings(Long customerId);

    List<Booking> getBookingsBySalon(Long salonId);

    Booking getBookingById(Long bookingId) throws Exception;

    Booking updateBookingStatus(Long bookingId, BookingStatus status) throws Exception;

    List<Booking> getBookingsByDate(LocalDate date, Long salonId);

    SalonReport getSalonReport(Long salonId);
}
