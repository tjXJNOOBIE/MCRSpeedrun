package com.tjxjnoobie.enums;

public enum Rank{
    MEMEBER("§7 "), // Gray color
    VIP("§a[VIP] "),        // Green color
    MOD("§9[Mod] "),        // Blue color
    ADMIN("§c[Admin] ");    // Red color

    private final String prefix;

    Rank(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}

