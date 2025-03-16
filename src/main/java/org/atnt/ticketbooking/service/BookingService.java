package org.atnt.ticketbooking.service;

import org.atnt.ticketbooking.dto.TicketChangeRequest;
import org.atnt.ticketbooking.dto.TicketRequest;
import org.atnt.ticketbooking.entity.Ticket;
import org.atnt.ticketbooking.entity.Showtime;
import org.atnt.ticketbooking.enums.BookingStatus;
import org.atnt.ticketbooking.exception.BookingException;
import org.atnt.ticketbooking.exception.ResourceNotFoundException;
import org.atnt.ticketbooking.repository.TicketRepository;
import org.atnt.ticketbooking.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@EnableScheduling
@Service
public class BookingService {

//    private static final int RESERVATION_TIMEOUT_MINUTES = 15;
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    private final Clock clock;

    @Autowired
    public BookingService(TicketRepository ticketRepository, ShowtimeRepository showtimeRepository, Clock clock) {
        this.ticketRepository = ticketRepository;
        this.showtimeRepository = showtimeRepository;
        this.clock = clock != null ? clock : Clock.systemDefaultZone();
    }

//    @Autowired
//    private final ScheduledExecutorService executorService;

//    @Autowired
//    public TicketService(ScheduledExecutorService executorService) {
//        this.executorService = executorService;
//    }

    @Transactional
    public Ticket bookTicket(TicketRequest request){
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

        if (showtime.getAvailableSeats() <= 0) {
            throw new BookingException("No available seats for showtime ID: " + showtime.getId());
        }
        if (ticketRepository.existsByShowtimeIdAndSeatNumber(request.getShowtimeId(), request.getSeatNumber())) {
            throw new BookingException("Seat " + request.getSeatNumber() + " is already booked for showtime ID: " + showtime.getId());
        }

        Ticket ticket = new Ticket();
        ticket.setShowtime(showtime);
        ticket.setUserId(request.getUserId());
        ticket.setSeatNumber(request.getSeatNumber());
        ticket.setPriceBooked(showtime.getPrice());
        ticket.setStatus(BookingStatus.BOOKED);
        ticket.setCreatedAt(LocalDateTime.now(clock));
        ticket.setUpdatedAt(LocalDateTime.now(clock));
        updateShowtimeAvailability(showtime.getId(), showtime.getAvailableSeats() - 1);
        showtimeRepository.save(showtime);


//        ticket.setReservationExpiry(LocalDateTime.now().plusMinutes(RESERVATION_TIMEOUT_MINUTES));

//        executorService.schedule(() -> cleanupExpiredReservation(savedTicket.getId()),
//                RESERVATION_TIMEOUT_MINUTES, TimeUnit.MINUTES);

        return ticketRepository.save(ticket);
    }

    @Transactional
    public void cancelBooking(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket ID " + ticketId + " not found"));

        Showtime showtime = ticket.getShowtime();
        if (showtime.getStartTime().minusHours(3).isBefore(LocalDateTime.now(clock))) {
            throw new BookingException("Cannot cancel ticket ID " + ticketId + " within 3 hours of showtime starting at " + showtime.getStartTime());
        }

        ticket.setStatus(BookingStatus.AVAILABLE);
        ticket.setUpdatedAt(LocalDateTime.now(clock));
        updateShowtimeAvailability(showtime.getId(), showtime.getAvailableSeats() + 1);

        ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket changeTicket(Long ticketId, TicketChangeRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket ID " + ticketId + " not found"));
        Showtime showtime = ticket.getShowtime();

        if (showtime.getStartTime().minusHours(3).isBefore(LocalDateTime.now(clock))) {
            throw new BookingException("Cannot change ticket ID " + ticketId + " within 3 hours of showtime starting at " + showtime.getStartTime());
        }
        if (ticketRepository.existsByShowtimeIdAndSeatNumber(showtime.getId(), request.getNewSeatNumber())) {
            throw new BookingException("New seat " + request.getNewSeatNumber() + " is already booked for showtime ID: " + showtime.getId());
        }

        ticket.setSeatNumber(request.getNewSeatNumber());
        ticket.setUpdatedAt(LocalDateTime.now(clock));
        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<Ticket> getBookingHistory(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    private void updateShowtimeAvailability(Long showtimeId, int newAvailability) {
        Showtime showtime = showtimeRepository.findById(showtimeId).orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));
        showtime.setAvailableSeats(newAvailability);
        showtimeRepository.save(showtime);
    }

    public Optional<Ticket> findTicketByShowtimeIdAndSeatNumber(Long showtimeId, String seatNumber) {
        return ticketRepository.findByShowtimeIdAndSeatNumber(showtimeId, seatNumber);
    }
//    @Scheduled(fixedRate = 5000)
//    @Transactional
//    public void cleanupExpiredReservation(Long bookingId) {
//        Ticket ticket = ticketRepository.findById(bookingId).orElse(null);
//        if (ticket != null && ticket.isReservationExpired() &&
//                ticket.getStatus() == BookingStatus.CONFIRMED) {
//            cancelBooking(bookingId);
//        }
//    }
}