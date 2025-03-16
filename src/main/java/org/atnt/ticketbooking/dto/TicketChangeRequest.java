package org.atnt.ticketbooking.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TicketChangeRequest {
    private String newSeatNumber;
}