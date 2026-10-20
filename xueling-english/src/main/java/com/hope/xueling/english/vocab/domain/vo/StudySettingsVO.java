package com.hope.xueling.english.vocab.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudySettingsVO {
    private String timeZone;
    private int dailyNewLimit;
    private double desiredRetention;
}
