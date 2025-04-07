package com.hiru.salon.booking.service.impl;

import com.hiru.salon.booking.domain.BookingStatus;
import com.hiru.salon.booking.dto.BookingRequest;
import com.hiru.salon.booking.dto.SalonDTO;
import com.hiru.salon.booking.dto.ServiceDTO;
import com.hiru.salon.booking.dto.UserDTO;
import com.hiru.salon.booking.modal.Booking;
import com.hiru.salon.booking.modal.SalonReport;
import com.hiru.salon.booking.repository.BookingRepository;
import com.hiru.salon.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    public Booking createBooking(BookingRequest booking, UserDTO userDTO, SalonDTO salonDTO, Set<ServiceDTO> serviceDTOSet) throws Exception {
        int totalDuration = serviceDTOSet.stream()
                        .mapToInt(ServiceDTO::getDuration)
                        .sum();

        LocalDateTime bookingStartTime = booking.getStartTime();
        LocalDateTime bookingEndTime = bookingStartTime.plusMinutes(totalDuration);

        Boolean isSlotAvailable = isTimeSlotAvailable(salonDTO, bookingStartTime, bookingEndTime);

        double totalPrice = serviceDTOSet.stream()
                .mapToDouble(ServiceDTO::getPrice)
                .sum();

        Set<Long> idList = serviceDTOSet.stream()
                .map(ServiceDTO::getId)
                .collect(Collectors.toSet());

        Booking newBooking = new Booking();
        newBooking.setCustomerId(userDTO.getId());
        newBooking.setSalonId(salonDTO.getId());
        newBooking.setServiceIds(idList);
        newBooking.setStatus(BookingStatus.PENDING);
        newBooking.setStartTime(bookingStartTime);
        newBooking.setEndTime(bookingEndTime);
        newBooking.setTotalPrice(totalPrice);

        if (!isSlotAvailable) {
            throw new Exception("Time slow not available");
        }
        return bookingRepository.save(newBooking);
    }

    public Boolean isTimeSlotAvailable(SalonDTO salonDTO,
                                       LocalDateTime bookingStartTime,
                                       LocalDateTime bookingEndTime) throws Exception {
        LocalDateTime salonOpenTime = salonDTO.getOpenTime().atDate(bookingStartTime.toLocalDate());
        LocalDateTime salonCloseTime = salonDTO.getCloseTime().atDate(bookingEndTime.toLocalDate());

        List<Booking> existingBookings = getBookingsBySalon(salonDTO.getId());

        if (bookingStartTime.isBefore(salonOpenTime) || bookingEndTime.isBefore(salonCloseTime)) {
            throw new Exception("Booking time must be within working hours");
        }

        for (Booking existingBooking : existingBookings) {
            LocalDateTime existingBookingStartTime = existingBooking.getStartTime();
            LocalDateTime existingBookingEndTime = existingBooking.getEndTime();

            if (bookingStartTime.isBefore(existingBookingStartTime) && bookingEndTime.isAfter(existingBookingEndTime)) {
                throw new Exception("This slot is not available. Choose a different slot.");
            }

            if (bookingStartTime.isEqual(existingBookingStartTime) || bookingEndTime.isEqual(existingBookingEndTime)) {
                throw new Exception("This slot is not available. Choose a different slot.");
            }
        }
        return true;
    }

    @Override
    public List<Booking> getBookings(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Booking> getBookingsBySalon(Long salonId) {
        return bookingRepository.findBySalonId(salonId);
    }

    @Override
    public Booking getBookingById(Long bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            throw new Exception("Booking not found");
        }
        return booking;
    }

    @Override
    public Booking updateBookingStatus(Long bookingId, BookingStatus status) throws Exception {
        Booking booking = getBookingById(bookingId);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsByDate(LocalDate date, Long salonId) {
        List<Booking> allBookings = getBookingsBySalon(salonId);

        if (date == null) {
            return allBookings;
        }
        return allBookings.stream()
                .filter(booking -> isSameDate(booking.getStartTime(), date) ||
                        isSameDate(booking.getEndTime(), date))
                .collect(Collectors.toList());
    }

    private boolean isSameDate(LocalDateTime dateTime, LocalDate date) {
        return dateTime.toLocalDate().isEqual(date);
    }

    @Override
    public SalonReport getSalonReport(Long salonId) {
        List<Booking> bookings = getBookingsBySalon(salonId);
        Double totalEarnings = bookings.stream()
                .mapToDouble(Booking::getTotalPrice)
                .sum();

        Integer totalBookings = bookings.size();

        List<Booking> canceledBookings = bookings.stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.CANCELLED))
                .toList();

        Double totalRefund = canceledBookings.stream()
                .mapToDouble(Booking::getTotalPrice)
                .sum();

        SalonReport salonReport = new SalonReport();
        salonReport.setSalonId(salonId);
        salonReport.setSalonName(salonReport.getSalonName());
        salonReport.setCancelledBookings(canceledBookings.size());
        salonReport.setTotalEarnings(totalEarnings);
        salonReport.setTotalBookings(totalBookings);
        salonReport.setTotalRefund(totalRefund);

        return salonReport;
    }
}
