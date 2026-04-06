package com.tjxjnoobie.proxy.store;

public class StoreBackendException extends RuntimeException {

    private final boolean retryable;

    public StoreBackendException(String message, boolean retryable) {
        super(message);
        this.retryable = retryable;
    }

    public StoreBackendException(String message, boolean retryable, Throwable cause) {
        super(message, cause);
        this.retryable = retryable;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
