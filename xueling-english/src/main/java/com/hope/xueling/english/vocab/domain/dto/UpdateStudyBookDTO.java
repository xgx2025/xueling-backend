package com.hope.xueling.english.vocab.domain.dto;

import lombok.Data;

/**
 * 学习词书启停入参
 * @author 谢光湘
 * @since 2026/10/24
 */
@Data
public class UpdateStudyBookDTO {
    /**
     * 是否将该词书纳入学习计划
     */
    private Boolean enabled;
}
