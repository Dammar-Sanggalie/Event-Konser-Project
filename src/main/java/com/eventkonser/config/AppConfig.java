package com.eventkonser.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Application URLs Configuration
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String notificationUrl;
    private String finishUrl;
    private String errorUrl;
}
