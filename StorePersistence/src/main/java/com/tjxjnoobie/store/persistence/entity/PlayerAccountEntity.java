package com.tjxjnoobie.store.persistence.entity;

import com.tjxjnoobie.store.domain.model.AccountState;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "player_account")
public class PlayerAccountEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String minecraftUuid;

    @Column(nullable = false, unique = true, length = 32)
    private String currentUsername;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountState accountState = AccountState.ACTIVE;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String linkedIdentitiesJson = "{}";

    public Long getId() {
        return id;
    }

    public String getMinecraftUuid() {
        return minecraftUuid;
    }

    public void setMinecraftUuid(String minecraftUuid) {
        this.minecraftUuid = minecraftUuid;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    public AccountState getAccountState() {
        return accountState;
    }

    public void setAccountState(AccountState accountState) {
        this.accountState = accountState;
    }

    public String getLinkedIdentitiesJson() {
        return linkedIdentitiesJson;
    }

    public void setLinkedIdentitiesJson(String linkedIdentitiesJson) {
        this.linkedIdentitiesJson = linkedIdentitiesJson;
    }
}
