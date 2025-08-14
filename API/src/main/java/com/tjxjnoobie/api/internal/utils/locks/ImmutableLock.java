package com.tjxjnoobie.api.internal.utils.locks;


/**
 * A mutable storage container that allows a value to be written exactly once.
 * After assignment, the value becomes immutable.
 *
 * @param <O> The type of object to store immutably.
 */
public class ImmutableLock<O> {
    private O value;
    private boolean isSet = false;

    /**
     * Sets the value if it hasn't already been set.
     *
     * @param value The value to assign.
     * @throws IllegalStateException if the value is already set.
     */
    public void set(O value) {
        if (isSet) {
            throw new IllegalStateException("Value has already been set and cannot be changed.");
        }
        this.value = value;
        this.isSet = true;
    }

    /**
     * Returns the stored value.
     *
     * @return The assigned value.
     */
    public O get() {
        return value;
    }

    /**
     * Checks whether the value has been set.
     *
     * @return true if the value has been assigned, false otherwise.
     */
    public boolean isSet() {
        return isSet;
    }
}
