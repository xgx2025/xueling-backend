package com.hope.xueling.english.vocab.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudyStatsVO {
    private int todayReviewed;
    private int todayNew;
    private int learnedWords;
    private int dueCount;
    private int dueRecallAttempts;
    private Double dueRecallRate;
}
