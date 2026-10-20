package com.hope.xueling.english.vocab.domain.dto;

import lombok.Data;

@Data
public class SubmitStudyReviewDTO {
    private String wordId;
    private Integer rating;
    private Integer expectedVersion;
    private String requestId;
    private Integer elapsedMs;
}
