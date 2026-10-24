package com.hope.xueling.english.vocab.domain.vo;

import java.time.LocalDate;
import java.util.List;

/**
 * 学习日历视图，按月汇总打卡情况
 * @param month 月份，格式 yyyy-MM
 * @param today 用户时区下的今天
 * @param checkedInToday 今天是否已打卡
 * @param currentStreak 当前连续打卡天数
 * @param longestStreak 历史最长连续打卡天数
 * @param totalCheckIns 累计打卡天数
 * @param days 当月每日明细
 * @author 谢光湘
 * @since 2026/10/24
 */
public record StudyCalendarVO(String month, LocalDate today, boolean checkedInToday,
                              int currentStreak, int longestStreak, int totalCheckIns,
                              List<StudyCalendarDayVO> days) {}
