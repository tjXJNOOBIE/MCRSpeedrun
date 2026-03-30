package com.tjxjnoobie.api.platform.cache;

import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.annotations.PostConstruct;
import com.tjxjnoobie.api.interfaces.IRank;
import com.tjxjnoobie.api.interfaces.IRankCache;

import java.sql.SQLException;
import java.util.*;

public class RankCache implements IRankCache {

    public HashMap<UUID, String> rank = new HashMap<>();
    public HashMap<UUID, Integer> powerLevel = new HashMap<>();
    public HashMap<UUID, Set<String>> permissions = new HashMap<>();
    public List<String> allRanks = new ArrayList<>();
    @Inject private IRank rankClass;

    public RankCache() {

    }
    @PostConstruct
    private void init() {
        // This runs AFTER dependency injection
        if (rankClass != null) {
            allRanks.add(rankClass.getAllRanks());
            System.out.println("[RankCache] Initialized with all ranks from Rank class");
        } else {
            System.err.println("[RankCache] Warning: rankClass is still null after injection!");
        }
    }


    @Override
    public void addRankCache(UUID uuid) throws SQLException {
        String playerRank = rankClass.getRank(uuid);
        int pLevel = rankClass.getPowerLevel(uuid);
        Set<String> perms = rankClass.getPermissions(uuid);
        rank.put(uuid, playerRank);
        powerLevel.put(uuid, pLevel);
        permissions.put(uuid, perms);
        System.out.println(uuid.toString() + " Loaded with permissions: Rank: " + getCachedRank(uuid) + " Power Level: " + getCachedPowerLevel(uuid));
    }

    @Override
    public void removeRankCache(UUID uuid) {
        rank.remove(uuid);
        powerLevel.remove(uuid);
        permissions.remove(uuid);
        System.out.println(uuid.toString()+" Unloaded from rank cache");
    }

    @Override
    public String getCachedRank(UUID uuid) {

        return rank.get(uuid);
    }

    @Override
    public int getCachedPowerLevel(UUID uuid) {

        return powerLevel.get(uuid);
    }

    @Override
    public Set<String> getCachedPermissions(UUID uuid) {

        return permissions.get(uuid);
    }

    @Override
    public boolean hasCachedPermission(UUID uuid, String permission) {
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
        return getCachedRank(uuid).equals("Owner") || getCachedRank(uuid).equals("Developer")
                || getCachedRank(uuid).equals("HeadAdmin") || getCachedRank(uuid).equals("SrMod")
                || getCachedRank(uuid).equals("Mod") || getCachedRank(uuid).equals("Admin");

    }
    @Override
    public boolean isAdmin(UUID uuid){
        return getCachedRank(uuid).equals("Owner") || getCachedRank(uuid).equals("Developer")
                || getCachedRank(uuid).equals("HeadAdmin") || getCachedRank(uuid).equals("Admin");
    }
    @Override
    public boolean isDonor(UUID uuid){
        return getCachedRank(uuid).equals("Partner") || getCachedRank(uuid).equals("Premier")
                || getCachedRank(uuid).equals("Prime") || getCachedRank(uuid).equals("Premium")
                || getCachedRank(uuid).equals("Supporter");
    }
}
