package com.eventkonser.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Midtrans Configuration Properties
 * Bind properties dari application.properties dengan prefix "midtrans"
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "midtrans")
public class MidtransConfig {
    private String serverKey;
    private String clientKey;
    private String merchantId;
    private boolean isProduction;
    private String apiUrl;
}
