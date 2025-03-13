package org.atnt.ticketbooking.controller;

import org.atnt.ticketbooking.dto.BookingRequest;
import org.atnt.ticketbooking.entity.Booking;
import org.atnt.ticketbooking.exception.BookingException;
import org.atnt.ticketbooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking Management", description = "Operations pertaining to ticket booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    @Operation(summary = "Book a ticket", description = "Creates a new booking for a showtime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully booked ticket"),
            @ApiResponse(responseCode = "400", description = "Invalid input or booking error"),
            @ApiResponse(responseCode = "404", description = "Showtime not found")
    })
    public ResponseEntity<Booking> bookTicket(
            @Parameter(description = "Booking request details", required = true)
            @RequestBody BookingRequest request) {
        Booking booking = bookingService.bookTicket(
                request.getUserId(),
                request.getShowtimeId(),
                request.getSeatNumber()
        );
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel a booking", description = "Cancels an existing booking if within allowed time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully cancelled booking"),
            @ApiResponse(responseCode = "400", description = "Cancellation not allowed within 3 hours"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<Void> cancelBooking(
            @Parameter(description = "ID of the booking to cancel", required = true)
            @PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok().build();
    }
}
