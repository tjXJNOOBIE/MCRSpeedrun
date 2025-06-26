package com.tjxjnoobie.exceptions;

/**
 * Exception thrown when an invalid inventory type is provided
 */
public class InvalidInventoryTypeException extends InventoryException {
    
    public InvalidInventoryTypeException() {
        super("InventoryType cannot be null");
    }
    
    public InvalidInventoryTypeException(String message) {
        super(message);
    }
}