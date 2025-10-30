package com.eventkonser.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long orderId;
    private String paymentMethod; // transfer, e-wallet, credit_card
}