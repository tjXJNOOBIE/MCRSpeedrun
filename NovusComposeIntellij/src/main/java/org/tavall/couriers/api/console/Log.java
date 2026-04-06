package org.tavall.couriers.api.console;

public final class Log {
    private Log() {
    }

    public static void info(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void warn(String message) {
        System.out.println("[WARN] " + message);
    }

    public static void error(String message) {
        System.err.println("[ERROR] " + message);
    }
}
