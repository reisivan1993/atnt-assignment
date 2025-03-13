package org.atnt.ticketbooking.repository;

import org.atnt.ticketbooking.entity.Booking;
import org.atnt.ticketbooking.enums.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByShowtimeIdAndStatus(Long showtimeId, BookingStatus status);
    Optional<Booking> findByShowtimeIdAndSeatNumber(Long showtimeId, String seatNumber);
}