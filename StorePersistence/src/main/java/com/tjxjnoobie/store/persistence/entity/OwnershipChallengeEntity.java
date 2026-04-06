package com.tjxjnoobie.store.persistence.entity;

import com.tjxjnoobie.store.domain.model.OwnershipChallengeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "ownership_challenge")
public class OwnershipChallengeEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_account_id")
    private PlayerAccountEntity playerAccount;

    @Column(nullable = false)
    private String usernameSnapshot;

    @Column(nullable = false, length = 36)
    private String playerUuid;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OwnershipChallengeStatus status = OwnershipChallengeStatus.PENDING;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant verifiedAt;

    public Long getId() {
        return id;
    }

    public PlayerAccountEntity getPlayerAccount() {
        return playerAccount;
    }

    public void setPlayerAccount(PlayerAccountEntity playerAccount) {
        this.playerAccount = playerAccount;
    }

    public String getUsernameSnapshot() {
        return usernameSnapshot;
    }

    public void setUsernameSnapshot(String usernameSnapshot) {
        this.usernameSnapshot = usernameSnapshot;
    }

    public String getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(String playerUuid) {
        this.playerUuid = playerUuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OwnershipChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(OwnershipChallengeStatus status) {
        this.status = status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Instant verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}
