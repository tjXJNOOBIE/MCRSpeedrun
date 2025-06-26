package com.tjxjnoobie.exceptions;

/**
 * Exception thrown when an invalid slot number is provided
 */
public class InvalidSlotException extends InventoryException {
    
    public InvalidSlotException(int slot, int inventorySize) {
        super("Invalid slot: " + slot + ". Slot must be between 0 and " + (inventorySize - 1) + " for inventory size " + inventorySize);
    }
    
    public InvalidSlotException(String message) {
        super(message);
    }
}