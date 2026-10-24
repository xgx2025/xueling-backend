package com.hope.xueling.english.vocab.domain.vo;

import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * 今日学习数据：队列卡片与今日进度
 * @author 谢光湘
 * @since 2026/10/24
 */
@Data
public class TodayStudyVO {
    /**
     * 当前到期待复习数
     */
    private int dueCount;
    /**
     * 今日剩余可学新词数
     */
    private int newRemaining;
    /**
     * 今日评分次数
     */
    private int todayReviewed;
    /**
     * 今日新学词数
     */
    private int todayNew;
    /**
     * 今日是否已打卡
     */
    private boolean checkedIn;
    /**
     * 下一张卡的到期时间，队列为空时为 null
     */
    private Instant nextDueAt;
    /**
     * 今日学习队列，到期卡优先，其后为新词
     */
    private List<StudyQueueCardVO> cards;
}
