package com.hope.xueling.english.vocab.service;

import java.time.Instant;

/**
 * 复习排程算法抽象：根据上次卡片状态、评分与目标记忆率计算下次到期时间，便于替换或升级算法
 * @author 谢光湘
 * @since 2026/10/24
 */
public interface ReviewScheduler {
    /**
     * 排程算法版本标识，卡片状态与算法版本绑定，便于后续迁移
     * @return 算法版本字符串
     */
    String version();

    /**
     * 按评分推进一次排程
     * @param previousCardJson 上次持久化的卡片状态 JSON，首次学习为 null
     * @param rating 四档评分：1 忘记、2 模糊、3 记得、4 熟练
     * @param desiredRetention 目标记忆率（0 到 1）
     * @param reviewedAt 本次复习时刻
     * @return 排程结果
     */
    ScheduledReview review(String previousCardJson, int rating, double desiredRetention, Instant reviewedAt);

    /**
     * 排程结果
     * @param cardJson 序列化后的卡片状态，直接持久化到进度表
     * @param state 排程后的卡片状态：LEARNING、REVIEW
     * @param dueAt 下次到期时间
     */
    record ScheduledReview(String cardJson, String state, Instant dueAt) {}
}
