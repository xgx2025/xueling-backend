package com.hope.xueling.english.vocab.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 单词复习列表DTO
 * @author 谢光湘
 * @since 2026/1/29
 */
@Data
@NoArgsConstructor
public class ReviewWordListDTO {
    /**
     * 单词ID列表
     */
    private List<String> wordIds;
}
