package com.tjxjnoobie.api.platform.global.metadata.enums;

/**
 * Enum defining all available class-level settings with their types and defaults.
 */
public enum ClassSetting {
    EXCEPTION_WRAPPING_ENABLED(Boolean.class, false),
    LOG_ELIGIBLE_INJECTION(Boolean.class, false),
    TAGS(String[].class, new String[0]),
    LOG_VERBOSITY_LEVEL(Integer.class, 1),
    CACHE_INSTANCES(Boolean.class, true),
    LAZY_INITIALIZATION(Boolean.class, false),
    SINGLETON_SCOPE(Boolean.class, false),
    PROXY_CREATION_ENABLED(Boolean.class, true),
    DEPENDENCY_VALIDATION_STRICT(Boolean.class, false),
    PERFORMANCE_MONITORING(Boolean.class, false);

    private final Class<?> type;
    private final Object defaultValue;

    ClassSetting(Class<?> type, Object defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public Class<?> getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <T> T getDefaultValueAs(Class<T> expectedType) {
        if (!expectedType.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Setting " + this + " is of type " + type.getSimpleName() + 
                                             ", not " + expectedType.getSimpleName());
        }
        return (T) defaultValue;
    }

    /**
     * Validate that a value is compatible with this setting's type.
     */
    public boolean isValidValue(Object value) {
        if (value == null) return true; // null is always valid (falls back to default)
        return type.isInstance(value);
    }

    /**
     * Cast and validate a value for this setting.
     */
    @SuppressWarnings("unchecked")
    public <T> T castValue(Object value) {
        if (value == null) {
            return (T) defaultValue;
        }
        if (!isValidValue(value)) {
            throw new IllegalArgumentException("Value " + value + " is not compatible with setting " + 
                                             this + " (expected " + type.getSimpleName() + ")");
        }
        return (T) value;
    }
}