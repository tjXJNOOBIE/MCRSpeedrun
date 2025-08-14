package com.tjxjnoobie.api.exceptions;

/**
 * Exception thrown when invalid pagination parameters are provided
 */
public class InvalidPaginationException extends InventoryException {
    
    public InvalidPaginationException(int currentPage, int totalPages) {
        super("Invalid pagination: current page " + currentPage + ", total pages " + totalPages + 
              ". Both values must be greater than 0 and current page must not exceed total pages");
    }
    
    public InvalidPaginationException(String message) {
        super(message);
    }
}