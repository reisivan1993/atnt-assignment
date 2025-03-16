package org.atnt.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "movies")
public class Movie {
    @Id @Column(name = "movie_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "movie_title")
    private String movieTitle;
    private String genre;
    private Integer duration;
    private String rating;
    @Column(name = "release_year")
    private Integer releaseYear;
}
