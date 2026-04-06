package com.tjxjnoobie.website.account;

public record OwnershipChallengeStatusView(
        boolean verified,
        boolean expired,
        String username,
        String playerUuid
) {
}
