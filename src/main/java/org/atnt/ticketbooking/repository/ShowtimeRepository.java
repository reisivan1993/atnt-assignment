package org.atnt.ticketbooking.repository;

import org.atnt.ticketbooking.entity.Movie;
import org.atnt.ticketbooking.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByMovie(Movie movie);
}