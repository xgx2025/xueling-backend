package com.hope.xueling.english.vocab.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 从单词本中移除单词DTO
 * @author 谢光湘
 * @since 2026/1/26
 */
@Data
@NoArgsConstructor
public class RemoveWordsFromWordBookDTO {
    /**
     * 单词本ID
     */
    private String wordBookId;
    /**
     * 要移除的单词ID列表
     */
    private List<String> wordIds;
}
