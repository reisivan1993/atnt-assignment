package org.atnt.ticketbooking.repository;

import org.atnt.ticketbooking.entity.Showtime;
import org.atnt.ticketbooking.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsByShowtimeIdAndSeatNumber(Long Id, String seatNumber);
    Optional<Ticket> findByShowtimeIdAndSeatNumber(Long Id, String seatNumber);
    List<Ticket> findByUserId(Long userId); // Added to fetch tickets by userId
}