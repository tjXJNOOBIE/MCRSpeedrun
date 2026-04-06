package com.tjxjnoobie.website.api.dto;

import jakarta.validation.constraints.NotBlank;

public record SessionTokenRequest(
        @NotBlank String sessionToken
) {
}
