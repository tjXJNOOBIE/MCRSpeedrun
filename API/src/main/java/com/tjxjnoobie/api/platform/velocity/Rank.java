package com.tjxjnoobie.api.platform.velocity;

import com.tjxjnoobie.api.dependency.IDependencyInjectableConcrete;
import com.tjxjnoobie.api.dependency.annotations.DelegatesToInterface;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.interfaces.IRank;
import com.tjxjnoobie.api.managers.MySQL;
import com.tjxjnoobie.api.managers.PlayerProfile;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
@DelegatesToInterface(getLinkedInterface = IRank.class)
public class Rank implements IRank, IDependencyInjectableConcrete {

    @Inject private PlayerProfile playerProfile;
    public List<String> ranks = new ArrayList<>();



    public void setRank(UUID uuid,String name, String rank) throws SQLException {
            setRank(uuid, rank);
        }

    @Override
    public void setRankFromUsername(String username, String rank) throws SQLException {

        MySQL.executePreparedStatement("UPDATE player_profile SET `RANK` = ? WHERE `NAME` = ?", rank, username);

    }

    @Override
    public void setRank(UUID uuid, String rank) throws SQLException {
        MySQL.executePreparedStatement("UPDATE player_profile SET `RANK` = ? WHERE `UUID` = ?", rank, uuid.toString());
    }

    @Override
    public void revokeRank(UUID uuid, String fallbackRankName) throws SQLException {
        String fallback = (fallbackRankName == null || fallbackRankName.isBlank()) ? "Member" : fallbackRankName;
        setRank(uuid, fallback);
    }
    @Override
    public String getRank(UUID uuid) throws SQLException {
        ResultSet rs = MySQL.getResult("SELECT `RANK` FROM player_profile WHERE `UUID` = ?", uuid.toString());
        if(rs != null && rs.next()){
            return rs.getString("RANK");
        }else {
            return "Couldn't get rank";
        }
    }
    @Override
    public int getPowerLevel(UUID uuid) throws SQLException {
        ResultSet rs = MySQL.getResult("SELECT `POWERLEVEL` FROM player_profile WHERE `UUID` = ?", uuid.toString());
        if(rs != null && rs.next()){
            return rs.getInt("POWERLEVEL");
        }else{
            return 1;
        }
    }
    @Override
    public Set<String> getPermissions(UUID uuid) throws SQLException {
        String query = "SELECT `PERMISSIONS` FROM player_profile WHERE `UUID` = ?";
        ResultSet rs = null;
        try {
            rs = MySQL.getResult(query, uuid.toString());
            if (rs != null && rs.next()) {
                String json = rs.getString("PERMISSIONS");
                if (json == null || json.isBlank() || json.equals("[]")) {
                    return Collections.emptySet();
                }
                // Assuming the JSON is a simple array of strings
                return new HashSet<>(Arrays.asList(json.substring(1, json.length() - 1).replace("\"", "").split(",")));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return Collections.emptySet();
    }

    // Sets or updates the permissions for a given player UUID
    public void setPermissions(UUID uuid, Set<String> permissions) throws SQLException {
        String query = "REPLACE INTO player_profile (`UUID`, `PERMISSIONS`) VALUES (?, ?)";
        String json = permissions.stream()
                .map(perm -> "\"" + perm + "\"")
                .collect(Collectors.joining(",", "[", "]"));
        MySQL.executePreparedStatement(query, uuid.toString(), json);
    }
    @Override
    public boolean hasPermission(UUID uuid, String permission) throws SQLException {
        Set<String> permissions = getPermissions(uuid);
        return permissions.contains(permission);
    }

    // Adds a single permission to the existing permissions of a player
    public void addPermission(UUID uuid, String permission) throws SQLException {
        Set<String> permissions = getPermissions(uuid);
        if (!permissions.contains(permission)) {
            permissions.add(permission);
            setPermissions(uuid, permissions);
        }else{
            System.out.println(uuid.toString()+ " already has permission '" + permission+"'");
        }
    }

    public static List<Object> getAllRanks(Object value) throws SQLException {
        List<Object> rowData = new ArrayList<>();
        String query = "SELECT * FROM ranks WHERE `RANK` = ?";

        ResultSet rs = MySQL.getResult(query, value);
        if (rs != null && rs.next()) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                rowData.add(rs.getObject(i));
            }
        }

        return rowData;
    }

    public String getAllRanks() {
        String query = "SELECT * FROM ranks WHERE `RANK` IS NOT NULL";
        try {
            ResultSet rs = MySQL.getResult(query);
            ranks.clear();
            String rank = "";
            while (rs != null && rs.next()) {
                rank = rs.getString("RANK");
                ranks.add(rank);
            }
            return rank;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


    }

    public List<String> getRanks(){
        return ranks;
    }
    }
