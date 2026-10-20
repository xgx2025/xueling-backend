package com.hope.xueling.english.vocab.domain.dto;

import lombok.Data;

@Data
public class UpdateStudySettingsDTO {
    private String timeZone;
    private Integer dailyNewLimit;
    private Double desiredRetention;
}
