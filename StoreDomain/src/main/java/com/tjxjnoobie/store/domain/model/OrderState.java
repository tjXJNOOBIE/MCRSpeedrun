package com.tjxjnoobie.store.domain.model;

public enum OrderState {
    PENDING_PAYMENT,
    PAID,
    FULFILLED,
    PARTIALLY_FULFILLED,
    FAILED,
    REFUNDED,
    CANCELLED
}
