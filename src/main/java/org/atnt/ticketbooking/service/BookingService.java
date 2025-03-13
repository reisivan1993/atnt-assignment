package org.atnt.ticketbooking.service;

import org.atnt.ticketbooking.entity.Booking;
import org.atnt.ticketbooking.entity.Showtime;
import org.atnt.ticketbooking.enums.BookingStatus;
import org.atnt.ticketbooking.exception.BookingException;
import org.atnt.ticketbooking.exception.ResourceNotFoundException;
import org.atnt.ticketbooking.repository.BookingRepository;
import org.atnt.ticketbooking.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class BookingService {

    private static final int RESERVATION_TIMEOUT_MINUTES = 15;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private final ScheduledExecutorService executorService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    public BookingService(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    private void validateSeatNumber(String seatNumber, Showtime showtime) throws BookingException {
        String row = seatNumber.substring(0, 1);
        int number = Integer.parseInt(seatNumber.substring(1));
        if (!row.matches("[A-J]") || number < 1 || number > 20) {
            throw new BookingException("Invalid seat number");
        }
    }

    private double calculatePrice(Showtime showtime) {
        return 10.0; // Simplified pricing logic
    }

    @Cacheable(value = "showtimes", key = "#showtimeId")
    public Showtime getShowtime(Long showtimeId) {
        return showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));
    }

    @CacheEvict(value = "showtimes", key = "#showtimeId")
    public void updateShowtimeAvailability(Long showtimeId, int newAvailability) {
        Showtime showtime = getShowtime(showtimeId);
        showtime.setAvailableSeats(newAvailability);
        showtimeRepository.save(showtime);
    }

    @Transactional
    public Booking bookTicket(String userId, Long showtimeId, String seatNumber) {
        Showtime showtime = getShowtime(showtimeId);

        if (bookingRepository.findByShowtimeIdAndSeatNumber(showtimeId, seatNumber).isPresent()) {
            throw new BookingException("Seat already booked");
        }

        if (showtime.getAvailableSeats() <= 0) {
            throw new BookingException("No seats available");
        }

        validateSeatNumber(seatNumber, showtime);

        double price = calculatePrice(showtime);
        if (!paymentService.processPayment(userId, price)) {
            throw new BookingException("Payment failed");
        }

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setShowtime(showtime);
        booking.setSeatNumber(seatNumber);
        booking.setPrice(price);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingTime(LocalDateTime.now());
        booking.setReservationExpiry(LocalDateTime.now().plusMinutes(RESERVATION_TIMEOUT_MINUTES));

        updateShowtimeAvailability(showtimeId, showtime.getAvailableSeats() - 1);

        Booking savedBooking = bookingRepository.save(booking);

        executorService.schedule(() -> cleanupExpiredReservation(savedBooking.getId()),
                RESERVATION_TIMEOUT_MINUTES, TimeUnit.MINUTES);

        return savedBooking;
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime showtimeStart = booking.getShowtime().getStartTime();

        if (Duration.between(now, showtimeStart).toHours() < 3) {
            throw new BookingException("Cannot cancel within 3 hours of showtime");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Showtime showtime = booking.getShowtime();
        updateShowtimeAvailability(showtime.getId(), showtime.getAvailableSeats() + 1);

        bookingRepository.save(booking);
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void cleanupExpiredReservation(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null && booking.isReservationExpired() &&
                booking.getStatus() == BookingStatus.CONFIRMED) {
            cancelBooking(bookingId);
        }
    }
}