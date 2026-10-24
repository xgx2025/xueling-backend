package com.hope.xueling.english.vocab.service.impl;

import com.hope.xueling.english.vocab.service.ReviewScheduler;
import io.github.openspacedrepetition.Card;
import io.github.openspacedrepetition.Rating;
import io.github.openspacedrepetition.Scheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * 基于 java-fsrs 的复习排程实现：关闭模糊化以保证同样的输入得到同样的排程结果，便于测试与回溯
 * @author 谢光湘
 * @since 2026/10/24
 */
@Component
public class FsrsReviewScheduler implements ReviewScheduler {
    /**
     * 四档评分到 FSRS 评级的映射，下标为评分减一：1 忘记、2 模糊、3 记得、4 熟练
     */
    private static final Rating[] RATINGS = {Rating.AGAIN, Rating.HARD, Rating.GOOD, Rating.EASY};

    /**
     * 返回排程算法版本标识，卡片状态与该版本绑定
     * @return 版本字符串
     */
    @Override
    public String version() {
        return "java-fsrs-1.0.0-no-fuzz";
    }

    /**
     * 按评分推进一次排程：首次学习以当前时刻作为初始到期时间，之后从序列化的卡片状态继续计算
     * @param previousCardJson 上次持久化的卡片状态 JSON，首次学习为 null
     * @param rating 四档评分，取值 1 至 4
     * @param desiredRetention 目标记忆率（0 到 1）
     * @param reviewedAt 本次复习时刻
     * @return 新的卡片状态、状态名与到期时间
     */
    @Override
    public ScheduledReview review(String previousCardJson, int rating, double desiredRetention, Instant reviewedAt) {
        Card card = previousCardJson == null
                ? Card.builder().due(reviewedAt).build()
                : Card.fromJson(previousCardJson);
        Scheduler scheduler = Scheduler.builder()
                .desiredRetention(desiredRetention)
                .enableFuzzing(false)
                .build();
        Card next = scheduler.reviewCard(card, RATINGS[rating - 1], reviewedAt).card();
        return new ScheduledReview(next.toJson(), next.getState().name(), next.getDue());
    }
}
