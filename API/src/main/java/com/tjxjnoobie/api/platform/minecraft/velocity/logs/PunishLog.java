package com.tjxjnoobie.api.platform.minecraft.velocity.logs;

import java.sql.Timestamp;

public class PunishLog {

    private final String uuid;
    private final String punishment;
    private final Timestamp startDate;
    private final Timestamp endDate;
    private final String sender;
    private final String punished;
    private final String reason;

    public PunishLog(String uuid, String punishment, Timestamp startDate,Timestamp endDate, String sender, String punished, String reason){
        this.uuid = uuid;
        this.punishment = punishment;
        this.endDate = endDate;
        this.startDate = startDate;
        this.sender = sender;
        this.punished = punished;
        this.reason = reason;
    }

    public String getPunishment() {
        return punishment;
    }

    public Timestamp getStartDate() {
        return startDate;
    }
    public Timestamp getEndDate() {
        return endDate;
    }

    public String getSender() {
        return sender;
    }

    public String getPunished() {
        return punished;
    }

    public String getReason() {
        return reason;
    }
}
