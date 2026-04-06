package com.tjxjnoobie.api.platform.cache;

import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.annotations.PostConstruct;
import com.tjxjnoobie.api.interfaces.IRank;
import com.tjxjnoobie.api.interfaces.IRankCache;

import java.sql.SQLException;
import java.util.*;

public class RankCache implements IRankCache {

    private static final HashMap<UUID, String> rank = new HashMap<>();
    private static final HashMap<UUID, Integer> powerLevel = new HashMap<>();
    private static final HashMap<UUID, Set<String>> permissions = new HashMap<>();
    private static final List<String> allRanks = new ArrayList<>();
    @Inject private IRank rankClass;

    public RankCache() {
        this.rankClass = new com.tjxjnoobie.api.platform.velocity.Rank();
    }

    public RankCache(IRank rankClass) {
        this.rankClass = rankClass;
    }

    @PostConstruct
    private void init() {
        // This runs AFTER dependency injection
        if (rankClass != null) {
            rankClass.getAllRanks();
            synchronized (allRanks) {
                allRanks.clear();
                allRanks.addAll(rankClass.getRanks());
            }
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
    public void refreshRankCache(UUID uuid) throws SQLException {
        removeRankCache(uuid);
        addRankCache(uuid);
    }

    @Override
    public String getCachedRank(UUID uuid) {

        return rank.get(uuid);
    }

    @Override
    public int getCachedPowerLevel(UUID uuid) {

        return powerLevel.getOrDefault(uuid, 0);
    }

    @Override
    public Set<String> getCachedPermissions(UUID uuid) {

        return permissions.getOrDefault(uuid, Collections.emptySet());
    }

    @Override
    public boolean hasCachedPermission(UUID uuid, String permission) {
        Set<String> userPermissions = permissions.get(uuid);
        return userPermissions != null && userPermissions.contains(permission);
    }

    @Override
    public boolean hasCachedRank(UUID uuid) {
        return rank.containsKey(uuid);
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
        if (allRanks.isEmpty()) {
            rankClass.getAllRanks();
            synchronized (allRanks) {
                allRanks.clear();
                allRanks.addAll(rankClass.getRanks());
            }
        }
        return allRanks.contains(rankName);
    }
    @Override
    public boolean isStaff(UUID uuid){
        String cachedRank = getCachedRank(uuid);
        return "Owner".equals(cachedRank) || "Developer".equals(cachedRank)
                || "HeadAdmin".equals(cachedRank) || "SrMod".equals(cachedRank)
                || "Mod".equals(cachedRank) || "Admin".equals(cachedRank);

    }
    @Override
    public boolean isAdmin(UUID uuid){
        String cachedRank = getCachedRank(uuid);
        return "Owner".equals(cachedRank) || "Developer".equals(cachedRank)
                || "HeadAdmin".equals(cachedRank) || "Admin".equals(cachedRank);
    }
    @Override
    public boolean isDonor(UUID uuid){
        String cachedRank = getCachedRank(uuid);
        return "Partner".equals(cachedRank) || "Premier".equals(cachedRank)
                || "Prime".equals(cachedRank) || "Premium".equals(cachedRank)
                || "Supporter".equals(cachedRank);
    }
}
