package com.tjxjnoobie.exceptions;

/**
 * Exception thrown when an invalid inventory size is provided
 */
public class InvalidInventorySizeException extends InventoryException {
    
    public InvalidInventorySizeException(int size) {
        super("Invalid inventory size: " + size + ". Size must be a positive multiple of 9 (9, 18, 27, 36, 45, 54)");
    }
    
    public InvalidInventorySizeException(String message) {
        super(message);
    }
}