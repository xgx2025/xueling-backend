package com.hope.xueling.english.vocab.domain.vo;

import java.time.LocalDate;
import java.util.List;

public record StudyCalendarVO(String month, LocalDate today, boolean checkedInToday,
                              int currentStreak, int longestStreak, int totalCheckIns,
                              List<StudyCalendarDayVO> days) {}
