package com.eventkonser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * DTO untuk User Management (Admin)
 * Tanpa circular references
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    @JsonProperty("idPengguna")
    private Long idPengguna;
    private String nama;
    private String email;
    private String noHp;
    private String role;
    private String alamat;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
