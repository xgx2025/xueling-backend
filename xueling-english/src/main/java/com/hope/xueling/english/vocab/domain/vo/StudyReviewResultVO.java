package com.hope.xueling.english.vocab.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class StudyReviewResultVO {
    private String wordId;
    private String state;
    private Instant dueAt;
    private int version;
}
