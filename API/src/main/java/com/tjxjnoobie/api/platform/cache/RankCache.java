package com.tjxjnoobie.api.platform.cache;

import com.tjxjnoobie.api.interfaces.IRankCache;
import com.tjxjnoobie.api.platform.minecraft.velocity.Rank;

import java.sql.SQLException;
import java.util.*;

public class RankCache implements IRankCache {

    public HashMap<UUID, String> rank = new HashMap<>();
    public HashMap<UUID, Integer> powerLevel = new HashMap<>();
    public HashMap<UUID, Set<String>> permissions = new HashMap<>();
    public List<String> allRanks = new ArrayList<>();
    private final Rank rankClass;

    public RankCache(Rank rankClass) throws SQLException {
        this.rankClass = rankClass;
        allRanks.add(rankClass.getAllRanks());
    }



    @Override
    public void addRankCache(UUID uuid) throws SQLException {
        String playerRank = rankClass.getRank(uuid);
        int pLevel = rankClass.getPowerLevel(uuid);
        Set<String> perms = rankClass.getPermissions(uuid);
        rank.put(uuid, playerRank);
        powerLevel.put(uuid, pLevel);
        permissions.put(uuid, perms);
        System.out.println(uuid.toString() + " Loaded with permissions: Rank: " + getRank(uuid) + " Power Level: " +getPowerLevel(uuid));
    }

    @Override
    public void removeRankCache(UUID uuid) {
        rank.remove(uuid);
        powerLevel.remove(uuid);
        permissions.remove(uuid);
        System.out.println(uuid.toString()+" Unloaded from rank cache");
    }

    @Override
    public String getRank(UUID uuid) {

        return rank.get(uuid);
    }

    @Override
    public int getPowerLevel(UUID uuid) {

        return powerLevel.get(uuid);
    }

    @Override
    public Set<String> getPermissions(UUID uuid) {

        return permissions.get(uuid);
    }

    @Override
    public boolean hasPermission(UUID uuid, String permission) {
        Set<String> userPermissions = permissions.get(uuid);
        return userPermissions != null && userPermissions.contains(permission);
    }

    @Override
    public HashMap<UUID, String> getRankCache() {
        return rank;
    }

    @Override
    public HashMap<UUID, Integer> getPowerLevelCache() {
        return powerLevel;
    }

    @Override
    public HashMap<UUID, Set<String>> getPermissionsCache() {
        return permissions;
    }

    @Override
    public boolean rankExists(String rankName) throws SQLException {
        return rankClass.getRanks().contains(rankName);
    }
    @Override
    public boolean isStaff(UUID uuid){
        return getRank(uuid).equals("Owner") || getRank(uuid).equals("Developer")
                || getRank(uuid).equals("HeadAdmin") || getRank(uuid).equals("SrMod")
                || getRank(uuid).equals("Mod") || getRank(uuid).equals("Admin");

    }
    @Override
    public boolean isAdmin(UUID uuid){
        return getRank(uuid).equals("Owner") || getRank(uuid).equals("Developer")
                || getRank(uuid).equals("HeadAdmin") || getRank(uuid).equals("Admin");
    }
    @Override
    public boolean isDonor(UUID uuid){
        return getRank(uuid).equals("Partner") || getRank(uuid).equals("Premier")
                || getRank(uuid).equals("Prime") || getRank(uuid).equals("Premium")
                || getRank(uuid).equals("Supporter");
    }
}
