package com.tjxjnoobie.api.managers;

import java.util.UUID;

public class RetentionProfile {

    private UUID uuid;
    private double spent;
    private double timePlayed;
    private int rejoins;
    private int purchases;
    private double rr;
    private String creator;
    public RetentionProfile(UUID uuid, double spent, double timePlayed, int rejoins, int purchases, String creator, double rr) {

    }
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public double getSpent() {
        return spent;
    }
    public String getCreator(){
        return creator;
    }
    public void setSpent(double spent) {
        this.spent = spent;
    }

    public double getTimePlayed() {
        return timePlayed;
    }
    public double getRR(){
        return rr;
    }
    public void setRR(double rr){
        this.rr = rr;
    }

    public void setTimePlayed(double timePlayed) {
        this.timePlayed = timePlayed;
    }

    public int getRejoins() {
        return rejoins;
    }

    public void setRejoins(int rejoins) {
        this.rejoins = rejoins;
    }

    public int getPurchases() {
        return purchases;
    }

    public void setPurchases(int purchases) {
        this.purchases = purchases;
    }


}
