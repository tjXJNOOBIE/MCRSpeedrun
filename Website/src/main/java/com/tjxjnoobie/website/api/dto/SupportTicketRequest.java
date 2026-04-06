package com.tjxjnoobie.website.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SupportTicketRequest(
        @NotBlank String fullName,
        @Email String email,
        String username,
        @NotBlank String issueType,
        @NotBlank String subject,
        @NotBlank String description
) {
}
