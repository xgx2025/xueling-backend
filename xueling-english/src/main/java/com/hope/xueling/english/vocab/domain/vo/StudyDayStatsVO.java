package com.hope.xueling.english.vocab.domain.vo;

import java.time.LocalDate;

/**
 * 单日学习统计，用于近 N 天学习记录展示
 * @param date 日期（用户时区）
 * @param reviewCount 当日评分次数
 * @param newWords 当日新学词数
 * @param studyTimeMs 当日近似学习时长（毫秒）
 * @param dueRecallAttempts 当日到期卡作答次数
 * @param dueRecallRate 到期卡回忆率，即评分大于忘记的占比；无样本时为 null
 * @param goodOrEasyRate 到期卡中评为记得或熟练的占比；无样本时为 null
 * @param dueBacklog 截至当日的历史到期积压数
 * @author 谢光湘
 * @since 2026/10/24
 */
public record StudyDayStatsVO(LocalDate date, int reviewCount, int newWords,
                              long studyTimeMs, int dueRecallAttempts,
                              Double dueRecallRate, Double goodOrEasyRate,
                              int dueBacklog) {}
