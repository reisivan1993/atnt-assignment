package org.atnt.ticketbooking.repository;

import org.atnt.ticketbooking.entity.Movie;
import org.atnt.ticketbooking.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}