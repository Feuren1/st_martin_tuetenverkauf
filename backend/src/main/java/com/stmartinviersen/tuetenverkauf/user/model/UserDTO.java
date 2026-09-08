package com.stmartinviersen.tuetenverkauf.user.model;

import java.util.Set;

public record UserDTO(Long id, String email, String firstName, String lastName, Set<UserRole> roles) {
}
