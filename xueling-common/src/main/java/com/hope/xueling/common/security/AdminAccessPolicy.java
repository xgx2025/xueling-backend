package com.hope.xueling.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理员白名单策略：账号角色落库前的临时方案，仅配置中的用户 ID 视为管理员，为空表示没有管理员。
 * <p>依次读取 xueling.admin.user-ids、xueling.feedback.admin-user-ids、
 * 环境变量 XUELING_ADMIN_IDS、XUELING_FEEDBACK_ADMIN_IDS，取第一个非空值，逗号分隔。</p>
 * @author 谢光湘
 * @since 2026/10/24
 */
@Component
public class AdminAccessPolicy {
    private final Set<Long> adminUserIds;

    /**
     * 解析管理员白名单配置
     * @param configuredIds 逗号分隔的用户 ID 配置值
     */
    public AdminAccessPolicy(@Value("${xueling.admin.user-ids:${xueling.feedback.admin-user-ids:${XUELING_ADMIN_IDS:${XUELING_FEEDBACK_ADMIN_IDS:}}}}") String configuredIds) {
        adminUserIds = configuredIds == null || configuredIds.isBlank()
                ? Set.of()
                : Arrays.stream(configuredIds.split(","))
                        .map(String::trim)
                        .filter(value -> !value.isEmpty())
                        .map(Long::parseLong)
                        .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * 判断用户是否在管理员白名单内
     * @param userId 用户 ID
     * @return 是否为管理员
     */
    public boolean isAdmin(long userId) {
        return adminUserIds.contains(userId);
    }
}
