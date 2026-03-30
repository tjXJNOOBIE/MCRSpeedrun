package com.tjxjnoobie.api.platform.velocity.logs;

import com.tjxjnoobie.api.interfaces.IPunishLog;

import java.sql.Timestamp;

public class PunishLog implements IPunishLog {

    private String uuid;
    private String punishment;
    private Timestamp startDate;
    private Timestamp endDate;
    private String sender;
    private String punished;
    private String reason;

   public PunishLog(){

   }

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
    }//testing comment
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
