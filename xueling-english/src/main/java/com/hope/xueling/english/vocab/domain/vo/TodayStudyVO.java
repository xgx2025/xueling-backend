package com.hope.xueling.english.vocab.domain.vo;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class TodayStudyVO {
    private int dueCount;
    private int newRemaining;
    private int todayReviewed;
    private int todayNew;
    private boolean checkedIn;
    private Instant nextDueAt;
    private List<StudyQueueCardVO> cards;
}
