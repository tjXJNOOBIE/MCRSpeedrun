package com.tjxjnoobie.exceptions;

/**
 * Exception thrown when an invalid page number is provided
 */
public class InvalidPageNumberException extends InventoryException {
    
    public InvalidPageNumberException(int page) {
        super("Invalid page number: " + page + ". Page number must be greater than 0");
    }
    
    public InvalidPageNumberException(String message) {
        super(message);
    }
}