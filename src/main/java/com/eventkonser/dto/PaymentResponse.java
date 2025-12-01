package com.eventkonser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO untuk Midtrans Snap Token Creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String snapToken;
    private String redirectUrl;
}
