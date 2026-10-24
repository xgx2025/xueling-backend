package com.hope.xueling.english.vocab.domain.dto;

import lombok.Data;

/**
 * 更新学习设置入参
 * @author 谢光湘
 * @since 2026/10/24
 */
@Data
public class UpdateStudySettingsDTO {
    /**
     * IANA 时区标识，如 Asia/Shanghai，决定“今天”的边界
     */
    private String timeZone;
    /**
     * 每日新词上限
     */
    private Integer dailyNewLimit;
    /**
     * 目标记忆率（0 到 1 之间），越高复习间隔越短
     */
    private Double desiredRetention;
}
