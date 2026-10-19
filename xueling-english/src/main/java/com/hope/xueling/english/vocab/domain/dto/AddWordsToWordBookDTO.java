package com.hope.xueling.english.vocab.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * 将单词添加到单词本DTO
 * @author 谢光湘
 * @since 2026/1/26
 */
@Data
@NoArgsConstructor
public class AddWordsToWordBookDTO {
    /**
     * 单词本ID
     */
    private String wordBookId;
    /**
     * 单词ID列表
     */
    private List<String> wordIds;
}
