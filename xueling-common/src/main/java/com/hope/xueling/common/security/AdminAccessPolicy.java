package com.hope.xueling.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/** Temporary admin allowlist until account roles are persisted. Empty means no admins. */
@Component
public class AdminAccessPolicy {
    private final Set<Long> adminUserIds;

    public AdminAccessPolicy(@Value("${xueling.admin.user-ids:${xueling.feedback.admin-user-ids:${XUELING_ADMIN_IDS:${XUELING_FEEDBACK_ADMIN_IDS:}}}}") String configuredIds) {
        adminUserIds = configuredIds == null || configuredIds.isBlank()
                ? Set.of()
                : Arrays.stream(configuredIds.split(","))
                        .map(String::trim)
                        .filter(value -> !value.isEmpty())
                        .map(Long::parseLong)
                        .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isAdmin(long userId) {
        return adminUserIds.contains(userId);
    }
}
