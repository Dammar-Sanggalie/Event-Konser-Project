package com.eventkonser.dto;

import lombok.Data;

/**
 * DTO untuk update order status dari admin
 */
@Data
public class UpdateOrderStatusRequest {
    private String statusPembayaran;  // Payment status: PENDING, SUCCESS, FAILED, EXPIRED, REFUNDED
    private String orderStatus;       // Order status: PENDING, PAID, USED, CANCELLED, EXPIRED, REFUNDED
}
