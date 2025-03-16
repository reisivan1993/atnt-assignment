package org.atnt.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "showtimes",
        indexes = {
                @Index(name = "idx_showtime_movie_start", columnList = "movie_id,startTime")
        })
public class Showtime {
    @Id @Column(name = "showtime_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;
    private String theater;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "max_seats")
    private Integer maxSeats;
    @Column(name = "available_Seats")
    private Integer availableSeats;
    @Column(name = "price")
    private Double price;
}
