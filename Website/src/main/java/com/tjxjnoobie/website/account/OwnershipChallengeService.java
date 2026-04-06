package com.tjxjnoobie.website.account;

import com.tjxjnoobie.store.domain.model.AccountState;
import com.tjxjnoobie.store.domain.model.ActorType;
import com.tjxjnoobie.store.domain.model.AuditRecord;
import com.tjxjnoobie.store.domain.model.OwnershipChallengeResult;
import com.tjxjnoobie.store.domain.model.OwnershipChallengeStatus;
import com.tjxjnoobie.store.domain.model.UsernameResolution;
import com.tjxjnoobie.store.domain.service.AuditService;
import com.tjxjnoobie.store.domain.service.PlayerIdentityService;
import com.tjxjnoobie.store.integration.dto.OwnershipVerificationRequest;
import com.tjxjnoobie.store.integration.dto.OwnershipVerificationResult;
import com.tjxjnoobie.store.persistence.entity.OwnershipChallengeEntity;
import com.tjxjnoobie.store.persistence.entity.PlayerAccountEntity;
import com.tjxjnoobie.store.persistence.repository.OwnershipChallengeRepository;
import com.tjxjnoobie.store.persistence.repository.PlayerAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class OwnershipChallengeService {

    private final PlayerIdentityService playerIdentityService;
    private final PlayerAccountRepository playerAccountRepository;
    private final OwnershipChallengeRepository ownershipChallengeRepository;
    private final AuditService auditService;
    private final SecureRandom secureRandom = new SecureRandom();

    public OwnershipChallengeService(PlayerIdentityService playerIdentityService,
                                     PlayerAccountRepository playerAccountRepository,
                                     OwnershipChallengeRepository ownershipChallengeRepository,
                                     AuditService auditService) {
        this.playerIdentityService = playerIdentityService;
        this.playerAccountRepository = playerAccountRepository;
        this.ownershipChallengeRepository = ownershipChallengeRepository;
        this.auditService = auditService;
    }

    @Transactional
    public OwnershipChallengeResult issueChallenge(String username) {
        if (!playerIdentityService.isValidMinecraftUsername(username)) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid Minecraft username");
        }
        UsernameResolution resolution = playerIdentityService.resolveUsername(username);
        PlayerAccountEntity playerAccount = getOrCreatePlayer(resolution);

        OwnershipChallengeEntity challenge = new OwnershipChallengeEntity();
        challenge.setPlayerAccount(playerAccount);
        challenge.setUsernameSnapshot(resolution.normalizedUsername());
        challenge.setPlayerUuid(resolution.uuid().toString());
        challenge.setCode(generateCode());
        challenge.setStatus(OwnershipChallengeStatus.PENDING);
        challenge.setExpiresAt(Instant.now().plusSeconds(600));
        ownershipChallengeRepository.save(challenge);

        auditService.record(new AuditRecord(
                ActorType.PLAYER,
                resolution.uuid().toString(),
                "OWNERSHIP_CHALLENGE_ISSUED",
                "ownership_challenge",
                String.valueOf(challenge.getId()),
                challenge.getCode(),
                "{\"username\":\"" + resolution.normalizedUsername() + "\"}"
        ));

        return new OwnershipChallengeResult(
                challenge.getId(),
                resolution.normalizedUsername(),
                challenge.getCode(),
                true
        );
    }

    @Transactional(readOnly = true)
    public OwnershipChallengeStatusView getStatus(String code) {
        Optional<OwnershipChallengeEntity> verified = ownershipChallengeRepository.findByCodeAndStatusAndExpiresAtAfter(
                code,
                OwnershipChallengeStatus.VERIFIED,
                Instant.now()
        );
        if (verified.isPresent()) {
            OwnershipChallengeEntity challenge = verified.get();
            return new OwnershipChallengeStatusView(
                    true,
                    false,
                    challenge.getUsernameSnapshot(),
                    challenge.getPlayerUuid()
            );
        }

        Optional<OwnershipChallengeEntity> pending = ownershipChallengeRepository.findByCodeAndStatusAndExpiresAtAfter(
                code,
                OwnershipChallengeStatus.PENDING,
                Instant.now()
        );
        if (pending.isPresent()) {
            OwnershipChallengeEntity challenge = pending.get();
            return new OwnershipChallengeStatusView(
                    false,
                    false,
                    challenge.getUsernameSnapshot(),
                    challenge.getPlayerUuid()
            );
        }

        return new OwnershipChallengeStatusView(false, true, null, null);
    }

    @Transactional(readOnly = true)
    public Optional<UsernameResolution> getVerifiedResolution(String code) {
        return ownershipChallengeRepository.findByCodeAndStatusAndExpiresAtAfter(
                        code,
                        OwnershipChallengeStatus.VERIFIED,
                        Instant.now()
                )
                .map(entity -> new UsernameResolution(
                        UUID.fromString(entity.getPlayerUuid()),
                        entity.getUsernameSnapshot()
                ));
    }

    @Transactional
    public OwnershipVerificationResult verifyFromGame(OwnershipVerificationRequest request) {
        Optional<OwnershipChallengeEntity> challengeOptional = ownershipChallengeRepository.findByCodeAndStatusAndExpiresAtAfter(
                request.code(),
                OwnershipChallengeStatus.PENDING,
                Instant.now()
        );
        if (challengeOptional.isEmpty()) {
            return new OwnershipVerificationResult(false, "Challenge not found or already expired.");
        }

        OwnershipChallengeEntity challenge = challengeOptional.get();
        if (!challenge.getPlayerUuid().equalsIgnoreCase(request.playerUuid())) {
            return new OwnershipVerificationResult(false, "Verification code does not belong to this player.");
        }

        if (request.playerUsername() != null && !request.playerUsername().isBlank()) {
            PlayerAccountEntity account = challenge.getPlayerAccount();
            account.setCurrentUsername(request.playerUsername());
            playerAccountRepository.save(account);
            challenge.setUsernameSnapshot(request.playerUsername());
        }

        challenge.setStatus(OwnershipChallengeStatus.VERIFIED);
        challenge.setVerifiedAt(Instant.now());
        ownershipChallengeRepository.save(challenge);

        auditService.record(new AuditRecord(
                ActorType.INTEGRATION,
                request.playerUuid(),
                "OWNERSHIP_CHALLENGE_VERIFIED",
                "ownership_challenge",
                String.valueOf(challenge.getId()),
                challenge.getCode(),
                "{\"username\":\"" + challenge.getUsernameSnapshot() + "\"}"
        ));

        return new OwnershipVerificationResult(true, "Verification completed.");
    }

    private PlayerAccountEntity getOrCreatePlayer(UsernameResolution resolution) {
        return playerAccountRepository.findByMinecraftUuid(resolution.uuid().toString())
                .map(existing -> {
                    existing.setCurrentUsername(resolution.normalizedUsername());
                    return playerAccountRepository.save(existing);
                })
                .or(() -> playerAccountRepository.findByCurrentUsernameIgnoreCase(resolution.normalizedUsername())
                        .map(existing -> {
                            existing.setMinecraftUuid(resolution.uuid().toString());
                            existing.setCurrentUsername(resolution.normalizedUsername());
                            existing.setAccountState(AccountState.ACTIVE);
                            return playerAccountRepository.save(existing);
                        }))
                .orElseGet(() -> {
                    PlayerAccountEntity player = new PlayerAccountEntity();
                    player.setMinecraftUuid(resolution.uuid().toString());
                    player.setCurrentUsername(resolution.normalizedUsername());
                    player.setAccountState(AccountState.ACTIVE);
                    player.setLinkedIdentitiesJson("{}");
                    return playerAccountRepository.save(player);
                });
    }

    private String generateCode() {
        byte[] bytes = new byte[4];
        secureRandom.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes).toUpperCase();
    }
}
