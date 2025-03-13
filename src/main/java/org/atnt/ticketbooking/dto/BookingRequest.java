package org.atnt.ticketbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    @NotBlank
    private String userId;

    @NotNull
    private Long showtimeId;

    @NotBlank
    @Pattern(regexp = "^[A-Z][0-9]{1,2}$", message = "Seat number must be like A1, B12, etc.")
    private String seatNumber;
}