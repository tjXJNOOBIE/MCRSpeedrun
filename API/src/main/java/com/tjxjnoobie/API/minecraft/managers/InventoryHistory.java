package com.tjxjnoobie.API.minecraft.managers;

import com.tjxjnoobie.enums.InventoryType;

public class InventoryHistory {
    
    private final InventoryType type;
    private final String title;
    private final int size;
    private final int page;
    private final long timestamp;
    
    public InventoryHistory(InventoryType type, String title, int size, int page) {
        this.type = type;
        this.title = title;
        this.size = size;
        this.page = page;
        this.timestamp = System.currentTimeMillis();
    }
    
    public InventoryHistory(InventoryType type, String title, int size) {
        this(type, title, size, 1);
    }
    
    public InventoryType getType() {
        return type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public int getSize() {
        return size;
    }
    
    public int getPage() {
        return page;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "InventoryHistory{" +
                "type=" + type +
                ", title='" + title + '\'' +
                ", size=" + size +
                ", page=" + page +
                ", timestamp=" + timestamp +
                '}';
    }
}