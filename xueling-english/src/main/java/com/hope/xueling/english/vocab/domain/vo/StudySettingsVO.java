package com.hope.xueling.english.vocab.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 学习设置视图
 * @author 谢光湘
 * @since 2026/10/24
 */
@Data
@AllArgsConstructor
public class StudySettingsVO {
    /**
     * IANA 时区标识，决定“今天”的边界
     */
    private String timeZone;
    /**
     * 每日新词上限
     */
    private int dailyNewLimit;
    /**
     * 目标记忆率（0 到 1），越高复习间隔越短
     */
    private double desiredRetention;
}
