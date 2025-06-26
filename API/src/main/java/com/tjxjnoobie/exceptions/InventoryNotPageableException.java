package com.tjxjnoobie.exceptions;

import com.tjxjnoobie.enums.InventoryType;

/**
 * Exception thrown when trying to paginate an inventory type that doesn't support pagination
 */
public class InventoryNotPageableException extends InventoryException {
    
    public InventoryNotPageableException(InventoryType type) {
        super("Inventory type " + type.name() + " does not support pagination");
    }
    
    public InventoryNotPageableException(String message) {
        super(message);
    }
}