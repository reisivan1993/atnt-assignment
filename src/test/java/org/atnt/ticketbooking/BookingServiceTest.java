package org.atnt.ticketbooking;

import org.atnt.ticketbooking.dto.TicketChangeRequest;
import org.atnt.ticketbooking.dto.TicketRequest;
import org.atnt.ticketbooking.entity.Movie;
import org.atnt.ticketbooking.entity.Showtime;
import org.atnt.ticketbooking.entity.Ticket;
import org.atnt.ticketbooking.enums.BookingStatus;
import org.atnt.ticketbooking.exception.BookingException;
import org.atnt.ticketbooking.exception.ResourceNotFoundException;
import org.atnt.ticketbooking.repository.MovieRepository;
import org.atnt.ticketbooking.repository.ShowtimeRepository;
import org.atnt.ticketbooking.repository.TicketRepository;
import org.atnt.ticketbooking.service.BookingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private Movie inception;
    private Movie darkKnight;
    private Showtime showtime1; // Cinema 1, Inception
    private Showtime showtime2; // Cinema 2, Inception
    private Showtime showtime3; // Cinema 1, Dark Knight

    @MockBean
    private Clock clock; // Mock the Clock bean

    @BeforeEach
    void setUp() {
        // Mock the clock to a specific time (e.g., 2025-03-16T14:00)
        Instant fixedInstant = Instant.parse("2025-03-16T14:00:00Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(fixedClock.getZone());

// Insert movies
        inception = new Movie();
        inception.setMovieTitle("Inception");
        inception.setGenre("Sci-Fi");
        inception.setDuration(148);
        inception.setRating("PG-13");
        inception.setReleaseYear(2010);
        inception = movieRepository.save(inception);
        movieRepository.flush();
        System.out.println("Inception ID: " + inception.getId());

        darkKnight = new Movie();
        darkKnight.setMovieTitle("The Dark Knight");
        darkKnight.setGenre("Action");
        darkKnight.setDuration(152);
        darkKnight.setRating("PG-13");
        darkKnight.setReleaseYear(2008);
        darkKnight = movieRepository.save(darkKnight);
        movieRepository.flush();
        System.out.println("Dark Knight ID: " + darkKnight.getId());

        // Insert showtimes
        showtime1 = new Showtime();
        showtime1.setMovie(inception);
        showtime1.setTheater("Cinema 1");
        showtime1.setStartTime(LocalDateTime.parse("2025-03-16T18:00:00"));
        showtime1.setEndTime(LocalDateTime.parse("2025-03-16T20:28:00"));
        showtime1.setMaxSeats(50);
        showtime1.setAvailableSeats(48);
        showtime1.setPrice(12.50);
        showtime1 = showtimeRepository.save(showtime1);
        showtimeRepository.flush();
        System.out.println("Showtime1 ID: " + showtime1.getId());

        showtime2 = new Showtime();
        showtime2.setMovie(inception);
        showtime2.setTheater("Cinema 2");
        showtime2.setStartTime(LocalDateTime.parse("2025-03-16T20:00:00"));
        showtime2.setEndTime(LocalDateTime.parse("2025-03-16T22:28:00"));
        showtime2.setMaxSeats(30);
        showtime2.setAvailableSeats(30);
        showtime2.setPrice(12.50);
        showtime2 = showtimeRepository.save(showtime2);
        showtimeRepository.flush();
        System.out.println("Showtime2 ID: " + showtime2.getId());

        showtime3 = new Showtime();
        showtime3.setMovie(darkKnight);
        showtime3.setTheater("Cinema 1");
        showtime3.setStartTime(LocalDateTime.parse("2025-03-16T15:00:00"));
        showtime3.setEndTime(LocalDateTime.parse("2025-03-16T17:32:00"));
        showtime3.setMaxSeats(50);
        showtime3.setAvailableSeats(49);
        showtime3.setPrice(12.50);
        showtime3 = showtimeRepository.save(showtime3);
        showtimeRepository.flush();
        System.out.println("Showtime3 ID: " + showtime3.getId());

        // Pre-book seats
        Ticket ticket1A1 = new Ticket();
        ticket1A1.setShowtime(showtime1);
        ticket1A1.setUserId(null);
        ticket1A1.setSeatNumber("A1");
        ticket1A1.setStatus(BookingStatus.BOOKED);
        ticket1A1.setPriceBooked(12.50);
        ticket1A1.setCreatedAt(LocalDateTime.parse("2025-03-15T10:00:00"));
        ticket1A1.setUpdatedAt(LocalDateTime.parse("2025-03-15T10:00:00"));
        ticketRepository.save(ticket1A1);
        ticketRepository.flush();

        Ticket ticket1A2 = new Ticket();
        ticket1A2.setShowtime(showtime1);
        ticket1A2.setUserId(null);
        ticket1A2.setSeatNumber("A2");
        ticket1A2.setStatus(BookingStatus.BOOKED);
        ticket1A2.setPriceBooked(12.50);
        ticket1A2.setCreatedAt(LocalDateTime.parse("2025-03-15T10:00:00"));
        ticket1A2.setUpdatedAt(LocalDateTime.parse("2025-03-15T10:00:00"));
        ticketRepository.save(ticket1A2);
        ticketRepository.flush();

        Ticket ticket3B1 = new Ticket();
        ticket3B1.setShowtime(showtime3);
        ticket3B1.setUserId(null);
        ticket3B1.setSeatNumber("B1");
        ticket3B1.setStatus(BookingStatus.BOOKED);
        ticket3B1.setPriceBooked(12.50);
        ticket3B1.setCreatedAt(LocalDateTime.parse("2025-03-15T10:00:00"));
        ticket3B1.setUpdatedAt(LocalDateTime.parse("2025-03-15T10:00:00"));
        ticketRepository.save(ticket3B1);
        ticketRepository.flush();
    }

    @Test
    void testBookTicket_Success() {
        TicketRequest request = new TicketRequest(101, showtime1.getId(), "A3");
        Ticket ticket = bookingService.bookTicket(request);

        assertNotNull(ticket.getId());
        assertEquals(101, ticket.getUserId());
        assertEquals("Inception", ticket.getShowtime().getMovie().getMovieTitle());
        assertEquals(showtime1.getId(), ticket.getShowtime().getId());
        assertEquals("A3", ticket.getSeatNumber());
        assertEquals(BookingStatus.BOOKED, ticket.getStatus());
        assertEquals(47, ticket.getShowtime().getAvailableSeats());
        assertEquals(12.50, ticket.getShowtime().getPrice());
    }

    @Test
    void testBookTicket_ShowtimeNotFound() {
        TicketRequest request = new TicketRequest(101, 999L, "A1");
        assertThrows(ResourceNotFoundException.class, () -> bookingService.bookTicket(request));
    }

    @Test
    void testBookTicket_NoAvailableSeats() {
        // Fill showtime2 (30 seats) completely
        for (int i = 1; i <= 30; i++) {
            TicketRequest request = new TicketRequest();
            request.setUserId(101);
            request.setShowtimeId(showtime2.getId());
            request.setSeatNumber("B" + i);
            bookingService.bookTicket(request);
        }

        TicketRequest request = new TicketRequest(101, showtime2.getId(), "B31");
        assertThrows(BookingException.class, () -> bookingService.bookTicket(request));
    }

    @Test
    void testBookTicket_SeatAlreadyBooked() {
        TicketRequest request = new TicketRequest(101, showtime1.getId(), "A1");
        assertThrows(BookingException.class, () -> bookingService.bookTicket(request));
    }

    @Test
    void testCancelTicket_Success() {
        // Book a ticket
        TicketRequest request = new TicketRequest(101, showtime1.getId(), "A5");
        Ticket ticket = bookingService.bookTicket(request);
        assertNotNull(ticket.getId());
        Showtime updatedShowtimeAfterBooking = showtimeRepository.findById(showtime1.getId()).get();
        assertEquals(47, updatedShowtimeAfterBooking.getAvailableSeats());

        bookingService.cancelBooking(ticket.getId());

        // Verify the ticket status
        Optional<Ticket> cancelledTicket = ticketRepository.findById(ticket.getId());
        assertTrue(cancelledTicket.isPresent());
        assertEquals(BookingStatus.AVAILABLE, cancelledTicket.get().getStatus());

        // Verify available seats incremented
        Showtime updatedShowtimeAfterCanceling = showtimeRepository.findById(showtime1.getId()).get();
        assertEquals(48, updatedShowtimeAfterCanceling.getAvailableSeats());
    }

    @Test
    void testCancelTicket_WithinThreeHours_Fails() {
        // Adjust showtime3 to be within 3 hours
        showtime3.setStartTime(LocalDateTime.now().plusMinutes(30));
        showtime3.setEndTime(showtime3.getStartTime().plusMinutes(152));
        showtimeRepository.save(showtime3);

        // Book a new ticket to cancel
        TicketRequest request = new TicketRequest();
        request.setUserId(101);
        request.setShowtimeId(showtime3.getId());
        request.setSeatNumber("B2");
        Ticket ticket = bookingService.bookTicket(request);

        assertThrows(BookingException.class, () -> bookingService.cancelBooking(ticket.getId()));
    }

    @Test
    void testChangeTicket_Success() {
        // Book a ticket
        TicketRequest request = new TicketRequest(101, showtime1.getId(), "A5");
        Ticket ticket = bookingService.bookTicket(request);
        assertNotNull(ticket.getId());
        Long ticketId = ticket.getId();

        // Change the ticket seat (mocked time is 2025-03-16T14:00, 4 hours before showtime)
        TicketChangeRequest changeRequest = new TicketChangeRequest("A6");
        Ticket updatedTicket = bookingService.changeTicket(ticketId, changeRequest);

        assertNotNull(updatedTicket.getId());
        assertEquals(ticketId, updatedTicket.getId());
        assertEquals("A6", updatedTicket.getSeatNumber());
        assertEquals(101, updatedTicket.getUserId());
        assertEquals(showtime1.getId(), updatedTicket.getShowtime().getId());

        // Verify the original seat is available
        Optional<Ticket> originalSeat = bookingService.findTicketByShowtimeIdAndSeatNumber(showtime1.getId(), "A5");
        assertFalse(originalSeat.isPresent(), "Original seat A5 should be available");
    }

    @Test
    void testChangeTicket_WithinThreeHours_Fails() {
        // Set the clock to 2025-03-16T16:00 (2 hours before showtime)
        Instant fixedInstant = Instant.parse("2025-03-16T16:00:00Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        // Book a ticket
        TicketRequest request = new TicketRequest(101, showtime1.getId(), "A5");
        Ticket ticket = bookingService.bookTicket(request);
        assertNotNull(ticket.getId());
        Long ticketId = ticket.getId();

        // Attempt to change the ticket (should fail)
        TicketChangeRequest changeRequest = new TicketChangeRequest("A6");
        assertThrows(BookingException.class, () -> bookingService.changeTicket(ticketId, changeRequest));
    }

    @Test
    void testChangeTicket_SeatAlreadyBooked() {
        // Book a ticket on A5
        TicketRequest request1 = new TicketRequest(101, showtime1.getId(), "A5");
        Ticket ticket1 = bookingService.bookTicket(request1);
        assertNotNull(ticket1.getId());

        // Book another ticket on A6
        TicketRequest request2 = new TicketRequest(102, showtime1.getId(), "A6");
        Ticket ticket2 = bookingService.bookTicket(request2);
        assertNotNull(ticket2.getId());

        // Try to change ticket1 to A6 (already booked)
        TicketChangeRequest changeRequest = new TicketChangeRequest("A6");
        assertThrows(BookingException.class, () -> bookingService.changeTicket(ticket1.getId(), changeRequest));
    }

    @Test
    void testCancelTicket_NotFound() {
        assertThrows(ResourceNotFoundException.class, () -> bookingService.cancelBooking(999L));
    }

    @Test
    void testUniqueConstraintViolation() {
        TicketRequest request1 = new TicketRequest(101, showtime1.getId(), "A3");
        Ticket ticket1 = bookingService.bookTicket(request1);

        assertNotNull(ticket1.getId());
        assertEquals(101, ticket1.getUserId());
        assertEquals("A3", ticket1.getSeatNumber());
        assertEquals(showtime1.getId(), ticket1.getShowtime().getId());

        TicketRequest request2 = new TicketRequest(102, showtime1.getId(), "A3");
        BookingException thrown = assertThrows(BookingException.class, () -> {
            bookingService.bookTicket(request2);
        });

        assertEquals("Seat A3 is already booked for showtime ID: " + showtime1.getId(), thrown.getMessage());

        Optional<Ticket> bookedTicket = bookingService.findTicketByShowtimeIdAndSeatNumber(showtime1.getId(), "A3");
        assertTrue(bookedTicket.isPresent());
        assertEquals(101, bookedTicket.get().getUserId());
    }

    @Test
    void testChangeTicket_NewSeatBooked() {
        // Book a new ticket
        TicketRequest request = new TicketRequest();
        request.setUserId(101);
        request.setShowtimeId(showtime1.getId());
        request.setSeatNumber("A3");
        Ticket ticket = bookingService.bookTicket(request);

        TicketChangeRequest changeRequest = new TicketChangeRequest();
        changeRequest.setNewSeatNumber("A2"); // A2 is already booked from setup

        assertThrows(BookingException.class, () -> bookingService.changeTicket(ticket.getId(), changeRequest));
    }

    @Test
    void testChangeTicket_NotFound() {
        TicketChangeRequest changeRequest = new TicketChangeRequest();
        changeRequest.setNewSeatNumber("A3");

        assertThrows(ResourceNotFoundException.class, () -> bookingService.changeTicket(999L, changeRequest));
    }

    @AfterEach
    void tearDown() {
        ticketRepository.deleteAll();
        showtimeRepository.deleteAll();
        movieRepository.deleteAll();
    }
}