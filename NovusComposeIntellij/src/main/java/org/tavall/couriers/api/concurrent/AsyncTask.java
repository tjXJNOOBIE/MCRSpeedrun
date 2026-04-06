package org.tavall.couriers.api.concurrent;

import org.tavall.couriers.api.console.Log;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public final class AsyncTask {

    private static final AtomicLong VT_COUNTER = new AtomicLong();

    private AsyncTask() {
    }

    public record ScopeOptions(String name) {
        public static ScopeOptions defaults() {
            return new ScopeOptions(null);
        }

        public ScopeOptions withName(String name) {
            return new ScopeOptions(name);
        }
    }

    public static <T> CompletableFuture<T> runFuture(Callable<? extends T> task, ScopeOptions options) {
        Objects.requireNonNull(task, "task");
        ScopeOptions opt = options == null ? ScopeOptions.defaults() : options;

        CompletableFuture<T> future = new CompletableFuture<>();
        Log.info("AsyncTask runFuture scheduled: " + taskLabel(task, opt));
        newThread(opt.name != null ? opt.name : "async-task", () -> {
            try {
                future.complete(task.call());
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });
        return future;
    }

    public static <T> CompletableFuture<T> runFuture(Callable<? extends T> task) {
        return runFuture(task, ScopeOptions.defaults());
    }

    public static Thread newThread(String baseName, Runnable runnable) {
        String name = baseName + "-" + VT_COUNTER.incrementAndGet();
        Thread thread = Thread.ofVirtual().name(name).start(runnable);
        Log.info("AsyncTask thread started: " + name);
        return thread;
    }

    public static String unwrapMessage(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null && current != current.getCause()) {
            current = current.getCause();
        }
        return current.getMessage() != null ? current.getMessage() : current.getClass().getSimpleName();
    }

    private static String taskLabel(Callable<?> task, ScopeOptions opt) {
        if (opt != null && opt.name != null && !opt.name.isBlank()) {
            return opt.name;
        }
        return task == null ? "async-task" : task.getClass().getSimpleName();
    }
}
