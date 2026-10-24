package com.hope.xueling.english.vocab.domain.vo;

import java.util.List;

/**
 * 词条熟悉度分布：按掌握阶段统计数量并返回分页明细
 * @param learnedCount 已学词总数
 * @param learningCount 学习中（尚未进入复习态）词数
 * @param consolidatingCount 巩固中词数
 * @param stableCount 已稳定词数
 * @param dueCount 当前已到期词数
 * @param wordCount 统计范围内的词总数
 * @param page 当前页码
 * @param pageSize 每页条数
 * @param words 当前页词条明细
 * @param attentionWords 需要关注的词条（遗忘次数多或长期不稳定）
 * @author 谢光湘
 * @since 2026/10/24
 */
public record StudyFamiliarityVO(int learnedCount, int learningCount, int consolidatingCount,
                                 int stableCount, int dueCount, int wordCount, int page,
                                 int pageSize, List<StudyFamiliarityWordVO> words,
                                 List<StudyFamiliarityWordVO> attentionWords) {}
