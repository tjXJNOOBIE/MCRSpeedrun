package com.tjxjnoobie.api.exceptions;

/**
 * Exception thrown when inventory creation fails
 */
public class InventoryCreationException extends InventoryException {
    
    public InventoryCreationException(String title, int size) {
        super("Failed to create inventory with title '" + title + "' and size " + size);
    }
    
    public InventoryCreationException(String message) {
        super(message);
    }
    
    public InventoryCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}