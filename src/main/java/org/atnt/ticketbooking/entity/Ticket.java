package org.atnt.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.atnt.ticketbooking.enums.BookingStatus;

import java.time.LocalDateTime;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Ticket {
    @Id @JoinColumn(name = "ticket_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "seat_number")
    private String seatNumber;
    @Column(name = "price_booked")
    private double priceBooked;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private LocalDateTime reservationExpiry;

    public boolean isReservationExpired() {
        return reservationExpiry != null &&
                LocalDateTime.now().isAfter(reservationExpiry);
    }
}