package com.hope.xueling.english.vocab.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 单词词性列表DTO(用于存储单词和词性列表)
 * @author 谢光湘
 * @since 2026/1/28
 */
@Data
@NoArgsConstructor
public class WordPosListDTO {
    /**
     * 单词和词性列表
     */
    private List<WordPosDTO> wordPosList;
}
