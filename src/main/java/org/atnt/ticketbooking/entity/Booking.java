package org.atnt.ticketbooking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.atnt.ticketbooking.enums.BookingStatus;

import java.time.LocalDateTime;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "bookings",
        indexes = {
                @Index(name = "idx_booking_showtime_seat", columnList = "showtime_id,seatNumber"),
                @Index(name = "idx_booking_user", columnList = "userId")
        })
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;

    private String seatNumber;
    private double price;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime bookingTime;
    private LocalDateTime reservationExpiry;

    public boolean isReservationExpired() {
        return reservationExpiry != null &&
                LocalDateTime.now().isAfter(reservationExpiry);
    }
}