package org.atnt.ticketbooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.atnt.ticketbooking.dto.TicketChangeRequest;
import org.atnt.ticketbooking.dto.TicketRequest;
import org.atnt.ticketbooking.entity.Ticket;

import org.atnt.ticketbooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking Management", description = "Operations pertaining to ticket booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/book")
    @Operation(summary = "Book a new ticket", description = "Creates a new ticket booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ticket successfully booked"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Showtime not found")
    })
    public ResponseEntity<Ticket> bookTicket(
            @Parameter(description = "Ticket booking request", required = true) @RequestBody TicketRequest request) {
        Ticket ticket = bookingService.bookTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    @DeleteMapping("/{ticketId}")
    @Operation(summary = "Cancel an existing ticket", description = "Cancels a ticket by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket successfully canceled"),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "400", description = "Cancellation not allowed")
    })
    public ResponseEntity<Void> cancelBooking(
            @Parameter(description = "ID of the ticket to cancel", required = true) @PathVariable Long ticketId) {
        bookingService.cancelBooking(ticketId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{ticketId}")
    @Operation(summary = "Change an existing ticket", description = "Updates a ticket’s details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket successfully updated"),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "400", description = "Change not allowed or seat unavailable")
    })
    public ResponseEntity<Ticket> changeBooking(
            @Parameter(description = "ID of the ticket to change", required = true) @PathVariable Long ticketId,
            @Parameter(description = "Ticket change request", required = true) @RequestBody TicketChangeRequest request) {
        Ticket updatedTicket = bookingService.changeTicket(ticketId, request);
        return ResponseEntity.ok(updatedTicket);
    }
}
