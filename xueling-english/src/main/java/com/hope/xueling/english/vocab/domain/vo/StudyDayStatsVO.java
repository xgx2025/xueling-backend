package com.hope.xueling.english.vocab.domain.vo;

import java.time.LocalDate;

public record StudyDayStatsVO(LocalDate date, int reviewCount, int newWords,
                              long studyTimeMs, int dueRecallAttempts,
                              Double dueRecallRate, int dueBacklog) {}
