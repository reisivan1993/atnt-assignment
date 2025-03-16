package org.atnt.ticketbooking.repository;

import org.atnt.ticketbooking.entity.Showtime;
import org.atnt.ticketbooking.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsByShowtimeIdAndSeatNumber(Long Id, String seatNumber);
    long countByShowtimeId(Long Id);
    List<Ticket> findByShowtime(Showtime showtime);
}