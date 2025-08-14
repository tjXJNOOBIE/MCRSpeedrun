/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.console;

import com.tjxjnoobie.api.console.style.LogColors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Log {

    private static final BlockingQueue<String> asyncQueue = new LinkedBlockingQueue<>();
    private static final Thread logThread;
    static {
        logThread = new Thread(() -> {
            while (true) {
                try {
                    String message = asyncQueue.take();
                    System.out.println(message);
                } catch (InterruptedException ignored) {}
            }
        }, "LogThread");
        logThread.setDaemon(true);
        logThread.start();
    }
    public static void success(String msg) {
        log("[SUCCESS] ",LogColors.GREEN, msg);
    }
    public static void info(String msg) {
        log("[INFO] ", LogColors.GREEN, msg);
    }

    public static void warn(String msg) {
        log("[WARN] ", LogColors.YELLOW, msg);
    }

    public static void error(String msg) {
        log("[ERROR] ", LogColors.RED, msg);
    }
    public static void critical(String msg) {
        log("[CRITICAL] ",LogColors.WHITE, LogColors.bgRed(msg));
    }

    private static void log(String level, String color, String msg) {
        String output = color + level + " " + msg + LogColors.RESET;
        asyncQueue.offer(output);
    }








}
