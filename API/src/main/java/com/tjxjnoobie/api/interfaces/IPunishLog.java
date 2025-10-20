package com.tjxjnoobie.api.interfaces;

import java.sql.Timestamp;

/**
 * Interface for punishment logging operations
 */
public interface IPunishLog {

     String getPunishment();
     Timestamp getStartDate();
     Timestamp getEndDate();
     String getSender();
     String getPunished();
     String getReason();
    }

