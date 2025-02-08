package com.ddang.family.service;

import com.ddang.global.exception.BadRequestException;
import com.ddang.global.exception.ErrorCode;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class FamilyInviteCodeManager {

    private static final String REDIS_INVITE_KEY_PREFIX = "invite:";
    private final RedisTemplate<String, String> redisTemplate;

    public FamilyInviteCodeManager(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<InviteCode> findExistingInviteCode(Long familyId) {
        Set<String> keys = redisTemplate.keys(REDIS_INVITE_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return Optional.empty();
        }
        return keys.stream()
                .filter(key -> familyId.equals(getFamilyIdFromKey(key)))
                .map(key -> new InviteCode(key.replace(REDIS_INVITE_KEY_PREFIX, ""), Duration.ofSeconds(getInviteCodeTTL(key))))
                .findFirst();
    }

    public InviteCode generateInviteCode(Long familyId) {
        InviteCode inviteCode;
        boolean isSet;
        do {
            inviteCode = InviteCode.generate();
            String redisKey = REDIS_INVITE_KEY_PREFIX + inviteCode.getCode();
            isSet = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(
                    redisKey, String.valueOf(familyId), inviteCode.getTtl()));
        } while (!isSet);
        return inviteCode;
    }

    private Long getFamilyIdFromKey(String key) {
        String familyIdStr = redisTemplate.opsForValue().get(key);
        return familyIdStr != null ? Long.valueOf(familyIdStr) : null;
    }

    private long getInviteCodeTTL(String key) {
        Long ttl = redisTemplate.getExpire(key);
        return (ttl != null && ttl > 0) ? ttl : 0;
    }

    public static class InviteCode {
        private final String code;
        private final Duration ttl;

        private InviteCode(String code, Duration ttl) {
            this.code = code;
            this.ttl = ttl;
        }

        public static InviteCode generate() {
            String code = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();
            return new InviteCode(code, Duration.ofMinutes(5));
        }

        public String getCode() {
            return code;
        }

        public Duration getTtl() {
            return ttl;
        }
    }
}