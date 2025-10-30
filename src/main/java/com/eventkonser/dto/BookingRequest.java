package com.eventkonser.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Long userId;
    private Long ticketId;
    private Integer quantity;
}
