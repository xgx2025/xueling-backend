package com.hope.xueling.english.vocab.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

/**
 * 提交评分后的排程结果
 * @author 谢光湘
 * @since 2026/10/24
 */
@Data
@AllArgsConstructor
public class StudyReviewResultVO {
    /**
     * 单词 ID（字符串）
     */
    private String wordId;
    /**
     * 评分后的卡片状态：LEARNING、REVIEW
     */
    private String state;
    /**
     * 下次到期时间
     */
    private Instant dueAt;
    /**
     * 评分后的卡片版本号
     */
    private int version;
}
