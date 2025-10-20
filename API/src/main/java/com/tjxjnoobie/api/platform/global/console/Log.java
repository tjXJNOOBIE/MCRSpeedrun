/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.global.console;

import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.console.style.LogColors;
import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.platform.global.metadata.AbstractLogMetaData;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Log extends AbstractLogMetaData<Log> {

    @Inject
    private IGlobalContext globalContext;
    private static final BlockingQueue<String> asyncQueue = new LinkedBlockingQueue<>();
    private static final Thread logThread;

    static {
        logThread = new Thread(() -> {
            while (true) {
                try {
                    String message = asyncQueue.take();
                    System.out.println(message);
                } catch (InterruptedException ignored) {
                }
            }
        }, "LogThread");
        logThread.setDaemon(true);
        logThread.start();
    }

    public static void success(String msg) {
        log("[SUCCESS] ", LogColors.GREEN, msg);
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
        log("[CRITICAL] ", LogColors.WHITE, LogColors.bgRed(msg));
    }

    private static void log(String level, String color, String msg) {
        String output = color + level + " " + msg + LogColors.RESET;
        asyncQueue.offer(output);
    }

    public static void exception(Throwable t) {
        if (t == null) {
            error("Exception: No exception to log, is it null? ");
            return;
        }

        Throwable root = rootCause(t);
        StackTraceElement[] stack = t.getStackTrace();
        StackTraceElement appTop = firstAppFrame(stack);

        // Main exception info
        error("Exception: " + t.getClass().getName() + (t.getMessage() != null ? ": " + t.getMessage() : ""));
        if (appTop != null) {
            error("Source: " + formatFrame(appTop));
        } else if (stack.length > 0) {
            error("Source: " + formatFrame(stack[0]));
        }

        // Cause chain with first app frame per cause
        if (root != t) {
            error("Cause chain:");
            Throwable current = t.getCause();
            while (current != null) {
                StackTraceElement causeAppFrame = firstAppFrame(current.getStackTrace());
                String causeInfo = current.getClass().getName() +
                        (current.getMessage() != null ? ": " + current.getMessage() : "");
                if (causeAppFrame != null) {
                    error("  -> " + causeInfo + " at " + formatFrame(causeAppFrame));
                } else {
                    error("  -> " + causeInfo);
                }
                current = current.getCause();
                if (current == root) break; // avoid infinite loops
            }
        }

        // App stack with metadata-aware verbosity
        int printed = 0;
        error("App stack:");
        Set<Class<?>> culpritClasses = new HashSet<>();

        for (StackTraceElement ste : stack) {
            if (isAppFrame(ste)) {
                error("  at " + formatFrame(ste));

                // Track culprit classes for summary
                try {
                    Class<?> frameClass = Class.forName(ste.getClassName());
                    culpritClasses.add(frameClass);
                } catch (ClassNotFoundException ignored) {
                }

                if (++printed >= 8) break;
            }
        }

        if (printed == 0) {
            int lim = Math.min(5, stack.length);
            for (int i = 0; i < lim; i++) {
                error("  at " + formatFrame(stack[i]));
            }
        }
        // Culprit summary using metadata
//        if (!culpritClasses.isEmpty()) {
//            error("Culprit summary (classes in exception):");
//            for (Class<?> clazz : culpritClasses) {
//                if (getAbstractClassMetaDataInstance() != null) {
//                    error("  " + clazz.getSimpleName() +
//                          " (registrations=" +   getRegistrationCount() +
//                          ", instantiations=" +   getInstantiationCount() +
//                          ", dependencies=" +   getAttachedDependencies().size() + ")");
//                } else {
//                    error("  " + clazz.getSimpleName() + " (metadata unavailable)");
//                }
//            }
//        }
    }

    private static boolean isAppFrame(StackTraceElement ste) {
        String cn = ste.getClassName();
        return cn.startsWith("com.tjxjnoobie.") || cn.startsWith("com.tjxnjoobie");
    }

    private static String formatFrame(StackTraceElement ste) {
        String file = ste.getFileName();
        int line = ste.getLineNumber();
        return ste.getClassName() + "." + ste.getMethodName() + "(" + (file != null ? file : "Unknown Source") + (line >= 0 ? ":" + line : "") + ")";
    }

    private static StackTraceElement firstAppFrame(StackTraceElement[] stack) {
        if (stack == null) return null;
        for (StackTraceElement ste : stack) {
            if (isAppFrame(ste)) return ste;
        }
        return null;
    }

    private static Throwable rootCause(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur;
    }

    /**
     * Print a summary of class metadata.
     */
    public void classSummary(Class<?> type) {
        if (type == null) {
            error("Class summary: <null type>");
            return;
        }
        info("=== Class Summary: " + getType().getSimpleName() + " ===");
        info("Structure: interface=" + isInterface() +
                ", abstract=" + isAbstract() +
                ", enum=" + isEnum() +
                ", constructors=" + getConstructorCount());

        info("Members: publicMethods=" + getPublicMethods().size() +
                ", protectedMethods=" + getProtectedMethods().size() +
                ", privateMethods=" + getPrivateMethods().size());

        info("Fields: public=" + getPublicFields().size() +
                ", protected=" + getProtectedFields().size() +
                ", private=" + getPrivateFields().size());

        info("DI Relations: providedAs=" + getProvidedAs().size() +
                ", attachedDependencies=" + getAttachedDependencies().size() +
                ", instantiatedBy=" + getInstantiatedBy().size());

        info("Counters: registrations=" + getRegistrationCount() +
                ", instantiations=" + getInstantiationCount());

        if (!getSettings().isEmpty()) {
            info("Settings: " + getSettings());
        }

        if (getTags().length > 0) {
            info("Tags: " + String.join(", ", getTags()));
        }

        // Show specific settings
        info("Exception Wrapping: " + isExceptionWrappingEnabled());
        info("Log Eligible Injection: " + isLogEligibleInjection());
        info("Log Verbosity Level: " + getLogVerbosityLevel());
        info("Cache Instances: " + isCacheInstances());
        info("Lazy Initialization: " + isLazyInitialization());
        info("Singleton Scope: " + isSingletonScope());
        info("Proxy Creation: " + isProxyCreationEnabled());
        info("Strict Validation: " + isDependencyValidationStrict());
        info("Performance Monitoring: " + isPerformanceMonitoring());
    }

    /**
     * Print statistics about class loading and metadata tracking.
     */
    public void classLoadingStats() {
        if (getAbstractClassMetaDataInstance() == null) {
            error("Class loading statistics unavailable - registry not injected");
            return;
        }

        info("=== Class Loading Statistics ===");
        info("Total loaded classes in JVM: " + getTotalLoadedClasses());
        info("Classes tracked in metadata: " + getTrackedClasses() +
                " (" + String.format("%.1f", getTrackingPercentage()) + "%)");
        info("Classes registered in DI: " + getRegisteredClasses() +
                " (" + String.format("%.1f", getRegistrationPercentage()) + "% of tracked)");
        info("Classes instantiated via DI: " + getInstantiatedClasses() +
                " (" + String.format("%.1f", getInstantiationPercentage()) + "% of tracked)");
        info("Summary: " + toString());
    }


}
