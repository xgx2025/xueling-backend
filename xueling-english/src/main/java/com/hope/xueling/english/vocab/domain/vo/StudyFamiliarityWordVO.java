package com.hope.xueling.english.vocab.domain.vo;

import java.time.Instant;

public record StudyFamiliarityWordVO(String wordId, String word, String meaning,
                                     String stage, boolean due, Instant dueAt,
                                     int lapseCount, Integer lastRating, Double stabilityDays) {}
