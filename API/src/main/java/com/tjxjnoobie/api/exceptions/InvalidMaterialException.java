package com.tjxjnoobie.api.exceptions;

/**
 * Exception thrown when an invalid material is provided
 */
public class InvalidMaterialException extends InventoryException {
    
    public InvalidMaterialException() {
        super("Material cannot be null");
    }
    
    public InvalidMaterialException(String operation) {
        super("Material cannot be null for operation: " + operation);
    }
    
    public InvalidMaterialException(String message, Throwable cause) {
        super(message, cause);
    }
}