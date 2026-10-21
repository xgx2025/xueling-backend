package com.hope.xueling.english.vocab.domain.vo;

import java.time.LocalDate;

public record StudyCalendarDayVO(LocalDate date, boolean checkedIn, int reviewCount,
                                 int newWords, long studyTimeMs) {}
