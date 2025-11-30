package com.eventkonser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO untuk update user role saja (dari admin panel)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleRequest {
    private String role;
}
